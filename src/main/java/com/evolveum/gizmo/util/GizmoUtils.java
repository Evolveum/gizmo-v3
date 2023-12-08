/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.gizmo.util;

import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.data.provider.AbstractTaskDataProvider;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.repository.CustomerRepository;
import com.evolveum.gizmo.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
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
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import sk.lazyman.gizmo.data.*;
import com.evolveum.gizmo.web.PageTemplate;
import com.evolveum.gizmo.web.error.PageError;

import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    public static Date addOneDay(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DAY_OF_YEAR, 1);

        return cal.getTime();
    }

    public static Date removeOneMilis(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.MILLISECOND, -1);

        return cal.getTime();
    }

    public static Date clearTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();

    }

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

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        if (part.getProject() != null) {
            Project p = part.getProject();
            sb.append(p.getName());
        }

        if (sb.length() != 0) {
            sb.append(delimiter);
        }

        sb.append(part.getName());

        return sb.toString();
    }

    public static LocalDate createWorkDefaultFrom() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate createWorkDefaultTo() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

//    public static LocalDate createWorkDefaultFrom() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(GizmoUtils.clearTime(new Date()));
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        return cal.getTime();
//    }
//
//    public static Date createWorkDefaultTo() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(createWorkDefaultFrom());
//
//        cal.add(Calendar.MONTH, 1);
//        cal.add(Calendar.MILLISECOND, -1);
//
//        return cal.getTime();
//    }

    public static <T extends Enum> IChoiceRenderer<T> createEnumRenderer(final Component component) {
        return new ChoiceRenderer<>() {

            @Override
            public Object getDisplayValue(T object) {
                return createLocalizedModelForEnum(object, component).getObject();
            }

            @Override
            public String getIdValue(T object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static <T extends Enum> IModel<String> createLocalizedModelForEnum(T value, Component comp) {
        String key = value != null ? value.getClass().getSimpleName() + "." + value.name() : "";
        return new StringResourceModel(key, comp, null);
    }

    public static <T extends Enum> IModel<List<T>> createReadonlyModelFromEnum(final Class<T> type) {
        return new IModel<List<T>>() {

            @Override
            public List<T> getObject() {
                List<T> list = new ArrayList<T>();
                Collections.addAll(list, type.getEnumConstants());

                return list;
            }
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
                return object != null ? object.getCustomerName() + " - " + object.getProjectName() + " - " + object.getPartName() : null;
            }

            @Override
            public String getIdValue(CustomerProjectPartDto object, int index) {
                return Integer.toString(index);
            }
        };
    }

    public static IModel<List<User>> createUsersModel(final PageTemplate page) {
        return new LoadableModel<>(false) {

            @Override
            protected List<User> load() {
                try {
                    UserRepository repository = page.getUserRepository();

                    return repository.findAllEnabledUsers();
                } catch (Exception ex) {
                    handleModelException(page, "Message.couldntLoadUsers", ex);
                }

                return new ArrayList<>();
            }
        };
    }

    public static IModel<List<CustomerProjectPartDto>> createCustomerProjectPartList(final PageTemplate page,
                                                                                     final boolean showCustomers,
                                                                                     final boolean showProjects,
                                                                                     final boolean showParts) {
        return new LoadableModel<>(false) {

            @Override
            protected List<CustomerProjectPartDto> load() {
                List<CustomerProjectPartDto> list = null;
                try {
                    if (showCustomers && showProjects && !showParts) {
                        List<CustomerProjectPartDto> dbList = listProjectsFromDb(page.getEntityManager());
                        list = listCustomersProjects(dbList);
                    } else if (showCustomers && showProjects && showParts) {
                        list = listProjectsFromDb(page.getEntityManager());
                    } else if (showCustomers && !showProjects && !showParts) {
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

        JPAQuery query = new JPAQuery(entityManager);
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
        QBean projection = Projections.bean(CustomerProjectPartDto.class, bindings);

        return query.select(projection).fetch();

    }

    private static void handleModelException(PageTemplate page, String message, Exception ex) {
        Logger LOG = LoggerFactory.getLogger(page.getClass());
        LOG.error("Exception occurred, {}, reason: {}", message, ex.getMessage());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exception occurred, {}", ex);
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

        Set<Integer> addedCustomers = new HashSet<>();
        Set<Integer> addedProjects = new HashSet<>();
        for (CustomerProjectPartDto dto : dbList) {
            if (!addedCustomers.contains(dto.getCustomerId())) {
                list.add(new CustomerProjectPartDto(dto.getCustomerName(), dto.getCustomerId()));
                addedCustomers.add(dto.getCustomerId());
            }
            if (!addedProjects.contains(dto.getProjectId())) {
                list.add(new CustomerProjectPartDto(dto.getCustomerName(), dto.getProjectName(),
                        dto.getCustomerId(), dto.getProjectId()));
                addedProjects.add(dto.getProjectId());
            }
            if (!addedProjects.contains(dto.getPartId())) {
                list.add(new CustomerProjectPartDto(dto.getCustomerName(), dto.getProjectName(), dto.getPartName(),
                        dto.getCustomerId(), dto.getProjectId(), dto.getPartId()));
                addedProjects.add(dto.getPartId());
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

        List<Predicate> predicates = AbstractTaskDataProvider.createPredicates(filter);

        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        JPAQuery query = new JPAQuery(entityManager);
        query.from(task).leftJoin(work.part.project);
        if (!predicates.isEmpty()) {
            BooleanBuilder where = new BooleanBuilder();
            where.orAllOf(predicates.toArray(new Predicate[predicates.size()]));
            query.where(where);
        }
        query.orderBy(task.date.asc());

        return query.fetch();
//        return query.list(task);
    }

    public static IColumn<AbstractTask, String> createAbstractTaskRealizatorColumn(PageTemplate page) {
        return new PropertyColumn<>(page.createStringResource("AbstractTask.realizator"),
                StringUtils.join(new Object[]{AbstractTask.F_REALIZATOR, User.M_FULL_NAME}, '.'));
    }

    public static IColumn<AbstractTask, String> createWorkInvoiceColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("Task.length")) {

            @Override
            public void populateItem(Item<ICellPopulator<AbstractTask>> cellItem, String componentId,
                                     IModel<AbstractTask> rowModel) {
                cellItem.add(new Label(componentId, createInvoiceModel(rowModel)));
            }
        };
    }

    private static IModel<String> createInvoiceModel(final IModel<AbstractTask> rowModel) {
        return (IModel<String>) () -> {
            AbstractTask task = rowModel.getObject();
            double length = task.getWorkLength();
            double invoice = 0;

            if (task instanceof Work) {
                Work work = (Work) task;
                invoice = work.getInvoiceLength();
            }

            return StringUtils.join(new Object[]{length, " (", invoice, ')'});
        };
    }

    public static IColumn<AbstractTask, String> createWorkProjectColumn(PageTemplate page) {
        return new AbstractColumn<>(page.createStringResource("Work.part")) {

            @Override
            public void populateItem(Item<ICellPopulator<AbstractTask>> cellItem, String componentId,
                                     IModel<AbstractTask> rowModel) {
                cellItem.add(new Label(componentId, createProjectModel(rowModel)));
            }
        };
    }

    private static IModel<String> createProjectModel(final IModel<AbstractTask> rowModel) {
        return (IModel<String>) () -> {
            AbstractTask task = rowModel.getObject();
            if (!(task instanceof Work)) {
                return null;
            }

            Work work = (Work) task;
            return GizmoUtils.describeProjectPart(work.getPart(), " - ");
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
        return new IModel<String>() {

            @Override
            public String getObject() {
                AbstractTask task = rowModel.getObject();
                if (!(task instanceof Log)) {
                    return null;
                }

                Log log = (Log) task;
                return log.getCustomer().getName();
            }
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

    public static JPAQuery createWorkQuery(EntityManager entityManager) {
        JPAQuery query = new JPAQuery(entityManager);
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
        return query;
    }

}
