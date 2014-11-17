package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.form.AreaFormGroup;
import sk.lazyman.gizmo.component.form.FormGroup;
import sk.lazyman.gizmo.dto.EmailDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;

/**
 * @author lazyman
 */
@MountPath("/app/email")
public class PageEmail extends PageAppTemplate {

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

    public PageEmail() {
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

//        ReportSearchSummary summary = new ReportSearchSummary(ID_SUMMARY);
//        add(summary);

        WebMarkupContainer summary = new WebMarkupContainer(ID_SUMMARY);
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
//            String xml = taskListBacking.getXMLSummary();
//            String html = "";
//            try {
//                html = ReportProcessor.processHTML(xml, JSFUtils.getResource("/xslt/htmlReport.xslt"));
//            } catch (Exception ex) {
//                logger.debug("Can't create html part from task list for email.", ex);
//            }
//
//            Properties props = System.getProperties();
//            // Specify the desired SMTP server
//            props.put("mail.smtp.host", OUTGOING_MAIL_SERVER_HOSTNAME);
//            props.put("mail.smtp.port", OUTGOING_MAIL_SERVER_PORT);
//
//            // create a new Session object
//            Session session = javax.mail.Session.getDefaultInstance(props, null);
//
//            StringBuilder subject = new StringBuilder();
//            subject.append("Report - ");
//            subject.append(project.getName());
//            subject.append(" - [");
//            subject.append(sdf.format(searchBean.getFromDate()));
//            subject.append(" - ");
//            subject.append(sdf.format(searchBean.getToDate()));
//            subject.append("]");
//            if (searchBean.getSelectedUserList() == null && searchBean.getRealizator() != null) {
//                subject.append(" ");
//                subject.append(searchBean.getRealizator().getFullName());
//            }
//
//            Message mimeMessage = createMimeMessage(session, subject.toString());
//            mimeMessage.setDisposition(MimeMessage.INLINE);
////             Create an "Alternative" Multipart message
//            Multipart mp = new MimeMultipart("alternative");
//
//            // textova cast
//            MimeBodyPart textBp = new MimeBodyPart();
//            textBp.setDisposition(MimeMessage.INLINE);
//            textBp.setContent("Please use mail client with HTML support.", "text/plain; charset=utf-8");
//            mp.addBodyPart(textBp);
//
//            //obalka na koment ak existuje
//            Multipart commentMultipart = null;
//            if (comment != null && !comment.isEmpty()) {
//                BodyPart bodyPart = new MimeBodyPart();
//                bodyPart.setDisposition(MimeMessage.INLINE);
//                DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(comment, "text/plain; charset=utf-8"));
//                bodyPart.setDataHandler(dataHandler);
//
//                commentMultipart = new MimeMultipart("mixed");
//                commentMultipart.addBodyPart(bodyPart);
//            }
//
//            // obalka na html cast
//            Multipart htmlMp = createHtmlPart(html);
//
//            // vlozenie do tela mailu
//            BodyPart htmlBp = new MimeBodyPart();
//            htmlBp.setDisposition(BodyPart.INLINE);
//            htmlBp.setContent(htmlMp);
//            if (commentMultipart == null) {
//                mp.addBodyPart(htmlBp);
//            } else {
//                commentMultipart.addBodyPart(htmlBp);
//                BodyPart all = new MimeBodyPart();
//                all.setDisposition(BodyPart.INLINE);
//                all.setContent(commentMultipart);
//                mp.addBodyPart(all);
//            }
//
//
//            // Finalne vlozenie MIME a odoslanie
//            mimeMessage.setContent(mp);
//            Transport.send(mimeMessage);
//
//            logEmail(taskListBacking, searchBean, true);
//
//            // it worked!
//            taskListBacking.setMessage("Thank you.  Your message to was successfully sent.");
//            clearFields();
//            logger.info("Mail message to: sent successfuly");
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSendEmail", ex, target);
        }
    }

    private void cancelPerformed(AjaxRequestTarget target) {
        setResponsePage(PageDashboard.class);
    }


//    private Multipart createHtmlPart(String html) throws IOException, MessagingException {
//        Multipart multipart = new MimeMultipart("related");
//
//        // HTML part
//        BodyPart bodyPart = new MimeBodyPart();
//        bodyPart.setDisposition(MimeMessage.INLINE);
//        DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(html, "text/html; charset=utf-8"));
//        bodyPart.setDataHandler(dataHandler);
//        multipart.addBodyPart(bodyPart);
//
//        MimeBodyPart mimeBodyPart = createImageBodyPart();
//        multipart.addBodyPart(mimeBodyPart);
//
//        return multipart;
//    }
//
//    private Message createMimeMessage(Session session, String subject)
//            throws AddressException, UnsupportedEncodingException, MessagingException {
//        Message mimeMessage = new MimeMessage(session);
//        mimeMessage.setFrom(new InternetAddress(OUTGOING_MAIL_FROM_ADDRESS));
//        InternetAddress iamailTo[] = convertEmailAddress(mailTo);
//
//        mimeMessage.setRecipients(Message.RecipientType.TO, iamailTo);
//        if (mailCc != null && !"".equals(mailCc.trim())) {
//            mimeMessage.setRecipients(Message.RecipientType.CC, convertEmailAddress(mailCc));
//        }
//        if (mailBcc != null && !"".equals(mailBcc.trim())) {
//            mimeMessage.setRecipients(Message.RecipientType.BCC, convertEmailAddress(mailBcc));
//        }
//        mimeMessage.setSubject(subject);
//
//        return mimeMessage;
//    }
//
//    private MimeBodyPart createImageBodyPart() throws MalformedURLException, MessagingException {
//        MimeBodyPart bodyPart = new MimeBodyPart();
//        URLDataSource imageDs = new URLDataSource(JSFUtils.getResource("/img/logo-medium.gif"));
//
//        // Initialize and add the image file to the html body part
//        bodyPart.setFileName("logo-medium.gif");
//        bodyPart.setText("logo-medium");
//        bodyPart.setDataHandler(new DataHandler(imageDs));
//        bodyPart.setHeader("Content-ID", "<logo-medium.gif@nlight.eu>");
//        bodyPart.setHeader("Content-Type", "image/gif; name=logo-medium.gif");
//        bodyPart.setDisposition(MimeMessage.INLINE);
//
//        return bodyPart;
//    }
//
//    /**
//     * Converts string representation of email address. Accepted forms of
//     * email are:
//     * user@email.org
//     * User Name<user@email.org>
//     * Whole string can contain multiple adresses separated by comma
//     *
//     * @param addr
//     * @return
//     * @throws UnsupportedEncodingException
//     * @throws AddressException
//     */
//    public static InternetAddress[] convertEmailAddress(String addr)
//            throws UnsupportedEncodingException, AddressException {
//        String addrArray[] = addr.split(",");
//        InternetAddress[] retval = new InternetAddress[addrArray.length];
//        for (int i = 0; i < addrArray.length; i++) {
//            if (addrArray[i].contains("<")) {
//                String address = addrArray[i].substring(addrArray[i].indexOf("<") + 1, addrArray[i].indexOf(">"));
//                String personal = addrArray[i].substring(0, addrArray[i].indexOf("<"));
//                retval[i] = new InternetAddress(address, personal);
//            } else {
//                retval[i] = new InternetAddress(addrArray[i]);
//            }
//        }
//
//        return retval;
//    }
}
