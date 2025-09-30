/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.util;

import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ProjectSearchSettings;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.repository.UserRepository;
import com.evolveum.gizmo.web.PageTemplate;
import com.evolveum.gizmo.web.error.PageError;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.csrf.CsrfToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * @author lazyman
 */
public class GizmoUtils {

    public static final int DESCRIPTION_SIZE = 3000;
    public static final String DATE_FIELD_FORMAT = "dd/mm/yyyy";
    public static final String BASIC_DATE_FORMAT = "EEE dd. MMM. yyyy";

    private static final Logger LOG = LoggerFactory.getLogger(GizmoUtils.class);

    public static String describeProject(Project project) {
        if (project == null) {
            return null;
        }

        return StringUtils.join(new Object[]{project.getCustomer().getName(), project.getName()}, " - ");
    }

    public static String describeProjectPart(Part part, String delimiter) {
        if (part == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (part.getProject() != null && part.getProject().getCustomer() != null) {
            Customer c = part.getProject().getCustomer();
            sb.append(c.getName());
        }

        if (!sb.isEmpty()) {
            sb.append(delimiter);
        }

        if (part.getProject() != null) {
            Project p = part.getProject();
            sb.append(p.getName());
        }

        if (!sb.isEmpty()) {
            sb.append(delimiter);
        }

        sb.append(part.getName());

        return sb.toString();
    }

    public static LocalDate createWorkDefaultFrom() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static boolean isNotHoliday(LocalDate date) {
        WorkingDaysProvider provider = new WorkingDaysProvider();
        List<HolidayDay> holidays = provider.getPublicHolidaysFor(date.getYear(), date.getMonthValue());
        return holidays.stream().noneMatch(holiday -> LocalDate.of(date.getYear(), holiday.getMonth(), holiday.getDay()).isEqual(date));
    }

    public static LocalDate createWorkDefaultTo() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate computeWorkTo(LocalDate from) {
        return from.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate computeWorkFrom(LocalDate from) {
        return from.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static <T extends Enum<?>> IModel<List<T>> createReadonlyModelFromEnum(final Class<T> type) {
        return () -> {
            List<T> list = new ArrayList<>();
            Collections.addAll(list, type.getEnumConstants());

            return list;
        };
    }

    public static IChoiceRenderer<User> createUserChoiceRenderer() {
        return new ChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(User object) {
                return object != null ? object.getFullName() : null;
            }

            @Override
            public String getIdValue(User object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static IChoiceRenderer<Customer> createCustomerChoiceRenderer() {
        return new ChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(Customer object) {
                return object != null ? object.getName() : null;
            }

            @Override
            public String getIdValue(Customer object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static IChoiceRenderer<CustomerProjectPartDto> createCustomerProjectPartRenderer() {
        return new ChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(CustomerProjectPartDto object) {
                if (object == null) {
                    return null;
                }
                if (object.getPartName() != null) {
                    return object.getCustomerName() + " - " + object.getProjectName() + " - " + object.getPartName();
                }
                if (object.getProjectName() != null) {
                    return object.getCustomerName() + " - " + object.getProjectName();
                }
                return object.getCustomerName();
            }

            @Override
            public String getIdValue(CustomerProjectPartDto object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static LoadableModel<List<User>> createUsersModel(final PageTemplate page, IModel<ReportFilterDto> model) {
        return new LoadableModel<>(false) {

            @Override
            protected List<User> load() {
                try {
                    UserRepository repository = page.getUserRepository();
                    if (model == null) {
                        return repository.findAllEnabledUsers();
                    }
                    return model.getObject().isIncludeDisabled() ? repository.listUsers() : repository.findAllEnabledUsers();
                } catch (Exception ex) {
                    handleModelException(page, "Message.couldntLoadUsers", ex);
                }

                return new ArrayList<>();
            }
        };
    }

    public static IChoiceRenderer<LabelPart> createCategoriesChoiceRenderer() {

        return new IChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(LabelPart l) {
                return l.getCode() + " — " + l.getName();
            }

            @Override
            public String getIdValue(LabelPart l, int index) {
                return String.valueOf(l.getId());
            }

            @Override
            public LabelPart getObject(String id, IModel<? extends List<? extends LabelPart>> choices) {
                Long lid = Long.valueOf(id);
                for (LabelPart lp : choices.getObject()) if (lp != null && lid.equals(lp.getId())) return lp;
                return null;
            }
        };
    }

   public static LoadableModel<List<LabelPart>> createCategoriesChoices(PageTemplate pageTemplate) {
       return new LoadableModel<>(false) {
           @Override protected List<LabelPart> load() { return pageTemplate.getLabelPartRepository().listLabels(); }
       };
   }

    public static LoadableModel<List<CustomerProjectPartDto>> createCustomerProjectPartList(final PageTemplate page,
                                                                                     IModel<ProjectSearchSettings> settings) {
        return new LoadableModel<>(false) {

            @Override
            protected List<CustomerProjectPartDto> load() {
                List<CustomerProjectPartDto> list = null;
                try {
                    ProjectSearchSettings searchSettings = settings.getObject();
                    if (searchSettings.isCustomerSearch() && searchSettings.isProjectSearch() && !searchSettings.isPartSearch()) {
                        List<CustomerProjectPartDto> dbList = listProjectsFromDb(page.getEntityManager());
                        list = listCustomersProjects(dbList);
                    } else if (searchSettings.isCustomerSearch() && searchSettings.isProjectSearch() && searchSettings.isPartSearch()) {
                        list = listProjectsFromDb(page.getEntityManager());
                    } else if (searchSettings.isCustomerSearch() && !searchSettings.isProjectSearch() && !searchSettings.isPartSearch()) {
                        list = listCustomers(page);
                    }
                } catch (Exception ex) {
                    handleModelException(page, "Message.couldntLoadProjectData", ex);
                }

                if (list == null) {
                    list = new ArrayList<>();
                }

                LOG.debug("Found {} items.", list.size());

                Collections.sort(list);

                return list;
            }
        };
    }

    private static List<CustomerProjectPartDto> listProjectsFromDb(EntityManager entityManager) {
        QCustomer customer = QCustomer.customer;
        QProject project = QProject.project;
        QPart part = QPart.part;

        JPAQuery<?> query = new JPAQuery<>(entityManager);
        query.from(customer).leftJoin(customer.projects, project).leftJoin(QProject.project.parts, part);
        query.where(QProject.project.closed.eq(false).and(part.id.isNotNull()));
        query.orderBy(customer.name.asc(), project.name.asc(), part.name.asc());

        Map<String, Expression<?>> bindings = new HashMap<>();
        bindings.put(CustomerProjectPartDto.F_CUSTOMER_ID, customer.id);
        bindings.put(CustomerProjectPartDto.F_CUSTOMER_NAME, customer.name);
        bindings.put(CustomerProjectPartDto.F_PROJECT_ID, project.id);
        bindings.put(CustomerProjectPartDto.F_PROJECT_NAME, project.name);
        bindings.put(CustomerProjectPartDto.F_PART_ID, part.id);
        bindings.put(CustomerProjectPartDto.F_PART_NAME, part.name);
        QBean<CustomerProjectPartDto> projection = Projections.bean(CustomerProjectPartDto.class, bindings);

        return query.select(projection).fetch();

    }

    private static void handleModelException(PageTemplate page, String message, Exception ex) {
        Logger LOG = LoggerFactory.getLogger(page.getClass());
        LOG.error("Exception occurred, {}, reason: {}", message, ex.getMessage());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exception occurred, {}", ex.getMessage(), ex);
        }

        PageError errorPage = new PageError();
        errorPage.error(page.createStringResource(message, ex.getMessage()).getString());
        throw new RestartResponseException(errorPage);
    }

    private static List<CustomerProjectPartDto> listCustomers(PageTemplate page) {
        List<CustomerProjectPartDto> list = new ArrayList<>();

        CustomerRepository repository = page.getCustomerRepository();
        List<Customer> customers = repository.listCustomersWithOpenProjects();
        for (Customer customer : customers) {
            list.add(new CustomerProjectPartDto(customer.getName(), customer.getId()));
        }

        return list;
    }

    private static List<CustomerProjectPartDto> listCustomersProjects(List<CustomerProjectPartDto> dbList) {
        List<CustomerProjectPartDto> list = new ArrayList<>();

        Set<Integer> addedProjects = new HashSet<>();
        for (CustomerProjectPartDto dto : dbList) {
            if (!addedProjects.contains(dto.getProjectId())) {
                list.add(new CustomerProjectPartDto(dto.getCustomerName(), dto.getProjectName(),
                        dto.getCustomerId(), dto.getProjectId()));
                addedProjects.add(dto.getProjectId());
            }
        }

        return list;
    }

    public static String formatDate(Date date) {
        return formatDate(date, BASIC_DATE_FORMAT);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static double sumInvoiceLength(final IModel<List<AbstractTask>> data) {
        List<AbstractTask> list = data.getObject();

        double sum = 0;
        for (AbstractTask task : list) {
            if (task instanceof Work) {
                sum += ((Work) task).getInvoiceLength();
            }
        }
        return sum;
    }

    public static double sumWorkLength(final IModel<List<AbstractTask>> data) {
        List<AbstractTask> list = data.getObject();

        double sum = 0;
        for (AbstractTask task : list) {
            sum += task.getWorkLength();
        }
        return sum;
    }

    public static List<AbstractTask> loadData(ReportFilterDto filter, EntityManager entityManager) {
        List<AbstractTask> data = new ArrayList<>();
        if (filter == null) {
            return data;
        }

        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        JPAQuery<AbstractTask> query = new JPAQuery<>(entityManager);
        query.from(task).leftJoin(work.part.project);

        query.where(ReportDataProvider.createPredicates(filter));

        query.orderBy(task.date.asc());

        return query.fetch();
    }

    public static IColumn<WorkDto, String> createAbstractTaskRealizatorColumn(PageTemplate page) {
        return new PropertyColumn<>(page.createStringResource("AbstractTask.realizator"),
                StringUtils.join(new Object[]{AbstractTask.F_REALIZATOR, User.M_FULL_NAME}, '.'));
    }

    public static IColumn<WorkDto, String> createWorkInvoiceColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("Task.length")) {

            @Override
            public void populateItem(Item<ICellPopulator<WorkDto>> cellItem, String componentId,
                                     IModel<WorkDto> rowModel) {
                cellItem.add(new Label(componentId, createInvoiceModel(rowModel)));
            }
        };
    }

    private static IModel<String> createInvoiceModel(final IModel<WorkDto> rowModel) {
        return () -> {
            WorkDto task = rowModel.getObject();
            double length = task.getWorkLength();
            double invoice = task.getInvoiceLength();

            String roundedLength = String.format("%.2g%n", length);
            String roundedInvoice = String.format("%.2g%n", invoice);

            return StringUtils.join(roundedLength, " (", roundedInvoice, ')');
        };
    }

    public static IColumn<WorkDto, String> createWorkProjectColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("Work.part")) {

            @Override
            public void populateItem(Item<ICellPopulator<WorkDto>> cellItem, String componentId,
                                     IModel<WorkDto> rowModel) {
                cellItem.add(new Label(componentId, createProjectModel(rowModel)));
            }
        };
    }

    private static IModel<String> createProjectModel(final IModel<WorkDto> rowModel) {
        return () -> {
            WorkDto task = rowModel.getObject();
            CustomerProjectPartDto customerProjectPartDto = task.getCustomerProjectPart().getFirst();
            return StringUtils.join(new Object[]{
                    customerProjectPartDto.getCustomerName(),
                    customerProjectPartDto.getProjectName(),
                    customerProjectPartDto.getPartName()}, "/");
        };
    }

    public static IColumn<AbstractTask, String> createLogCustomerColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("Log.customer")) {

            @Override
            public void populateItem(Item<ICellPopulator<AbstractTask>> cellItem, String componentId, IModel<AbstractTask> rowModel) {
                cellItem.add(new Label(componentId, createCustomerModel(rowModel)));
            }
        };
    }

    private static IModel<String> createCustomerModel(final IModel<AbstractTask> rowModel) {
        return () -> {
            AbstractTask task = rowModel.getObject();
            if (!(task instanceof Log log)) {
                return null;
            }

            return log.getCustomer().getName();
        };
    }

    public static String toSha1(String value) {
        if (value == null) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(value.getBytes(StandardCharsets.UTF_8));

            char[] array = Hex.encode(md.digest());
            return new String(array);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static JPAQuery<Work> createWorkQuery(EntityManager entityManager) {
        JPAQuery<Work> query = new JPAQuery<>(entityManager);
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
        return query;
    }

    public static IColumn<WorkDto, String> createWorkTimeRangeColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("AbstractTask.from")) {

            @Override
            public void populateItem(Item<ICellPopulator<WorkDto>> cellItem, String componentId,
                                     IModel<WorkDto> rowModel) {
                cellItem.add(new Label(componentId, createTimeRangeModel(rowModel)));
            }
        };
    }
    private static IModel<String> createTimeRangeModel(IModel<WorkDto> rowModel) {
        return new LoadableDetachableModel<>() {
            @Override
            protected String load() {
                WorkDto work = rowModel.getObject();
                LocalTime from = work.getFrom();
                LocalTime to = work.getTo();

                if (from == null || to == null) {
                    return "";
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return from.format(formatter) + " – " + to.format(formatter);
            }
        };
    }


    public static CsrfToken getCsrfToken() {
        Request req = RequestCycle.get().getRequest();
        HttpServletRequest httpReq = (HttpServletRequest) req.getContainerRequest();

        return (CsrfToken) httpReq.getAttribute("_csrf");
    }

}
