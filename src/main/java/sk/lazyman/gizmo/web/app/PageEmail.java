package sk.lazyman.gizmo.web.app;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.ReportSearchSummary;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.data.*;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.EmailDto;
import sk.lazyman.gizmo.dto.ReportSearchSummaryDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.repository.EmailLogRepository;
import sk.lazyman.gizmo.repository.ProjectRepository;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author lazyman
 */
@MountPath("/app/email")
public class PageEmail extends PageAppTemplate {

    private static final String MAIL_HOST = "mail.hostname";
    private static final String MAIL_PORT = "mail.port";
    private static final String MAIL_FROM = "mail.from";

    private static final String ID_FORM = "form";
    private static final String ID_TO = "to";
    private static final String ID_CC = "cc";
    private static final String ID_BCC = "bcc";
    private static final String ID_BODY = "body";
    private static final String ID_SEND = "send";
    private static final String ID_CANCEL = "cancel";
    private static final String ID_SUMMARY = "summary";
    private static final String ID_TABLE = "table";

    private IModel<EmailDto> model = new Model<>(new EmailDto());
    private IModel<WorkFilterDto> filter;
    private IModel<List<AbstractTask>> dataModel;

    public PageEmail() {
        this(new LoadableModel<WorkFilterDto>(false) {

            @Override
            protected WorkFilterDto load() {
                WorkFilterDto dto = new WorkFilterDto();
                dto.setFrom(GizmoUtils.createWorkDefaultFrom());
                dto.setTo(GizmoUtils.createWorkDefaultTo());

                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                return dto;
            }
        });
    }

    public PageEmail(final IModel<WorkFilterDto> filter) {
        this.filter = filter;
        this.dataModel = new LoadableModel<List<AbstractTask>>(false) {

            @Override
            protected List<AbstractTask> load() {
                return GizmoUtils.loadData(filter.getObject(), getEntityManager());
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        FormGroup to = new FormGroup(ID_TO, new PropertyModel<String>(model, EmailDto.F_TO),
                createStringResource("PageEmail.to"), true);
        form.add(to);

        FormGroup cc = new FormGroup(ID_CC, new PropertyModel<String>(model, EmailDto.F_CC),
                createStringResource("PageEmail.cc"), false);
        form.add(cc);

        FormGroup bcc = new FormGroup(ID_BCC, new PropertyModel<String>(model, EmailDto.F_BCC),
                createStringResource("PageEmail.bcc"), false);
        form.add(bcc);

        AreaFormGroup body = new AreaFormGroup(ID_BODY, new PropertyModel<String>(model, EmailDto.F_BODY),
                createStringResource("PageEmail.body"), false);
        form.add(body);

        ReportSearchSummary summary = new ReportSearchSummary(ID_SUMMARY, filter, dataModel);
        add(summary);

        WebMarkupContainer table = new WebMarkupContainer(ID_TABLE);
        add(table);

        initButtons(form);
    }

    private void initButtons(Form form) {
        AjaxSubmitButton send = new AjaxSubmitButton(ID_SEND, createStringResource("GizmoApplication.button.send")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }
        };
        form.add(send);

        AjaxButton cancel = new AjaxButton(ID_CANCEL, createStringResource("GizmoApplication.button.cancel")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                cancelPerformed(target);
            }
        };
        form.add(cancel);
    }

    private void sendPerformed(AjaxRequestTarget target) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", getPropertyValue(MAIL_HOST));
            props.put("mail.smtp.port", getPropertyValue(MAIL_PORT));

            Session session = javax.mail.Session.getDefaultInstance(props, null);
            Message mail = buildMail(session);
            Transport.send(mail);

            boolean logged = logEmail(true, target);

            PageDashboard next = new PageDashboard();
            next.success(getString("Message.emailSuccess"));
            if (!logged) {
                next.warn(getString("Message.emailWasNotLogged"));
            }
            setResponsePage(next);
        } catch (Exception ex) {
            logEmail(false, target);
            handleGuiException(this, "Message.couldntSendEmail", ex, target);
        }
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageDashboard.class);
    }

    private boolean logEmail(boolean success, AjaxRequestTarget target) {
        boolean logged = false;
        try {
            EmailLog log = new EmailLog();
            log.setSuccessful(success);
            GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
            log.setSender(principal.getUser());
            log.setSentDate(new Date());

            EmailDto dto = model.getObject();
            log.setMailTo(dto.getTo());
            log.setMailCc(dto.getCc());
            log.setMailBcc(dto.getBcc());

            log.setDescription(dto.getBody());

            WorkFilterDto filter = this.filter.getObject();
            log.setFromDate(filter.getFrom());
            log.setToDate(filter.getTo());

            Set<User> realizators = createRealizators(filter);
            log.setRealizatorList(realizators);

            log.setProjectList(createProjects());
            log.setCustomerList(createCustomers());
            log.setSummaryInvoice(GizmoUtils.sumInvoiceLength(dataModel));
            log.setSummaryWork(GizmoUtils.sumWorkLength(dataModel));

            EmailLogRepository repository = getEmailLogRepository();
            repository.save(log);

            logged = true;
        } catch (Exception ex) {
            handleGuiException(this, ex, target);
        }
        return logged;
    }

    private Set<Project> createProjects() {
        Set<Project> set = new HashSet<>();

        WorkFilterDto dto = filter.getObject();
        CustomerProjectPartDto cppDto = dto.getProject();
        if (cppDto == null || cppDto.getProjectId() == null) {
            return null;
        }

        ProjectRepository repository = getProjectRepository();
        Project project = repository.findOne(cppDto.getProjectId());
        if (project != null) {
            set.add(project);
        }

        return set.isEmpty() ? null : set;
    }

    private Set<Customer> createCustomers() {
        Set<Customer> set = new HashSet<>();

        WorkFilterDto dto = filter.getObject();
        CustomerProjectPartDto cppDto = dto.getProject();
        if (cppDto == null) {
            return null;
        }

        if (cppDto.getCustomerId() == null || cppDto.getProjectId() != null) {
            return null;
        }

        CustomerRepository repository = getCustomerRepository();
        Customer customer = repository.findOne(cppDto.getCustomerId());
        if (customer != null) {
            set.add(customer);
        }

        return set.isEmpty() ? null : set;
    }

    private Set<User> createRealizators(WorkFilterDto filter) {
        Set<User> realizators = new HashSet<>();
        realizators.add(filter.getRealizator());

        return realizators;
    }

    private Message buildMail(Session session) throws MessagingException, IOException {
        String subject = createSubject();
        Message mimeMessage = createMimeMessage(session, subject);
        mimeMessage.setDisposition(MimeMessage.INLINE);
        Multipart mp = new MimeMultipart("alternative");

        MimeBodyPart textBp = new MimeBodyPart();
        textBp.setDisposition(MimeMessage.INLINE);
        textBp.setContent("Please use mail client with HTML support.", "text/plain; charset=utf-8");
        mp.addBodyPart(textBp);

        Multipart commentMultipart = null;
        EmailDto dto = model.getObject();
        if (StringUtils.isNotEmpty(dto.getBody())) {
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDisposition(MimeMessage.INLINE);
            DataHandler dataHandler = new DataHandler(
                    new ByteArrayDataSource(dto.getBody(), "text/plain; charset=utf-8"));
            bodyPart.setDataHandler(dataHandler);

            commentMultipart = new MimeMultipart("mixed");
            commentMultipart.addBodyPart(bodyPart);
        }

        String html = createHtml();
        Multipart htmlMp = createHtmlPart(html);

        BodyPart htmlBp = new MimeBodyPart();
        htmlBp.setDisposition(BodyPart.INLINE);
        htmlBp.setContent(htmlMp);
        if (commentMultipart == null) {
            mp.addBodyPart(htmlBp);
        } else {
            commentMultipart.addBodyPart(htmlBp);
            BodyPart all = new MimeBodyPart();
            all.setDisposition(BodyPart.INLINE);
            all.setContent(commentMultipart);
            mp.addBodyPart(all);
        }
        mimeMessage.setContent(mp);

        return mimeMessage;
    }

    private String createHtml() {
        PageEmailPrint page = new PageEmailPrint(filter, dataModel);

        ComponentRenderer renderer = new ComponentRenderer();
        return renderer.renderComponent(page).toString();
    }

    private String createSubject() {
        WorkFilterDto dto = filter.getObject();

        String format = "dd. MMM. yyyy";
        String from = GizmoUtils.formatDate(dto.getFrom(), format);
        String to = GizmoUtils.formatDate(dto.getTo(), format);

        String project = dto.getProject() == null ? "*" : dto.getProject().getDescription();

        StringBuilder subject = new StringBuilder();
        subject.append("Report for ");
        subject.append(project);
        subject.append(" (");
        subject.append(from);
        subject.append(" - ");
        subject.append(to);
        subject.append(")");
        if (dto.getRealizator() != null) {
            subject.append(" ");
            subject.append(dto.getRealizator().getFullName());
        }

        return subject.toString();
    }

    private Multipart createHtmlPart(String html) throws IOException, MessagingException {
        Multipart multipart = new MimeMultipart("related");

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDisposition(MimeMessage.INLINE);
        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(html, "text/html; charset=utf-8"));
        bodyPart.setDataHandler(dataHandler);
        multipart.addBodyPart(bodyPart);

        return multipart;
    }

    private Message createMimeMessage(Session session, String subject)
            throws UnsupportedEncodingException, MessagingException {

        Message mimeMessage = new MimeMessage(session);
        String from = getPropertyValue(MAIL_FROM);
        mimeMessage.setFrom(new InternetAddress(from));

        EmailDto dto = model.getObject();

        InternetAddress mailTo[] = convertEmailAddress(dto.getTo());
        mimeMessage.setRecipients(Message.RecipientType.TO, mailTo);

        String mailCc = dto.getCc();
        if (StringUtils.isNotEmpty(mailCc)) {
            mimeMessage.setRecipients(Message.RecipientType.CC, convertEmailAddress(mailCc));
        }
        String mailBcc = dto.getBcc();
        if (StringUtils.isNotEmpty(mailBcc)) {
            mimeMessage.setRecipients(Message.RecipientType.BCC, convertEmailAddress(mailBcc));
        }
        mimeMessage.setSubject(subject);

        return mimeMessage;
    }

    /**
     * Converts string representation of email address. Accepted forms of
     * email are:
     * user@email.org
     * User Name<user@email.org>
     * Whole string can contain multiple addresses separated by comma
     *
     * @param addr
     * @return
     * @throws UnsupportedEncodingException
     * @throws AddressException
     */
    private InternetAddress[] convertEmailAddress(String addr) throws UnsupportedEncodingException, AddressException {
        String addrArray[] = addr.split(",");
        InternetAddress[] retval = new InternetAddress[addrArray.length];
        for (int i = 0; i < addrArray.length; i++) {
            if (addrArray[i].contains("<")) {
                String address = addrArray[i].substring(addrArray[i].indexOf("<") + 1, addrArray[i].indexOf(">"));
                String personal = addrArray[i].substring(0, addrArray[i].indexOf("<"));
                retval[i] = new InternetAddress(address, personal);
            } else {
                retval[i] = new InternetAddress(addrArray[i]);
            }
        }

        return retval;
    }

    private IModel<ReportSearchSummaryDto> createSummaryModel() {
        return new LoadableModel<ReportSearchSummaryDto>(false) {

            @Override
            protected ReportSearchSummaryDto load() {
                return new ReportSearchSummaryDto();
            }
        };
    }
}
