package sk.lazyman.gizmo.util;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Projections;
import com.mysema.query.types.QBean;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.*;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.repository.PartRepository;
import sk.lazyman.gizmo.repository.ProjectRepository;
import sk.lazyman.gizmo.repository.UserRepository;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.error.PageError;

import javax.persistence.EntityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lazyman
 */
public class GizmoUtils {

    public static final int DESCRIPTION_SIZE = 3000;
    public static final String DATE_FIELD_FORMAT = "dd/mm/yyyy";

    private static final Logger LOG = LoggerFactory.getLogger(GizmoUtils.class);

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

        return StringUtils.join(new Object[]{project.getName(), project.getCustomer().getName()}, " - ");
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

    public static Date createWorkDefaultFrom() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(GizmoUtils.clearTime(new Date()));
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    public static Date createWorkDefaultTo() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createWorkDefaultFrom());

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);

        return cal.getTime();
    }

    public static <T extends Enum> IChoiceRenderer<T> createEnumRenderer(final Component component) {
        return new IChoiceRenderer<T>() {
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
        return new AbstractReadOnlyModel<List<T>>() {

            @Override
            public List<T> getObject() {
                List<T> list = new ArrayList<T>();
                Collections.addAll(list, type.getEnumConstants());

                return list;
            }
        };
    }

    public static IChoiceRenderer<User> createUserChoiceRenderer() {
        return new IChoiceRenderer<User>() {

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
        return new IChoiceRenderer<Customer>() {

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

    public static IModel<List<User>> createUsersModel(final PageTemplate page) {
        return new LoadableModel<List<User>>(false) {

            @Override
            protected List<User> load() {
                try {
                    UserRepository repository = page.getUserRepository();

                    return repository.findAll(new Sort(Sort.Direction.ASC, User.F_GIVEN_NAME, User.F_FAMILY_NAME));
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
        return new LoadableModel<List<CustomerProjectPartDto>>(false) {

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
        query.where(QProject.project.closed.eq(false));
        query.orderBy(customer.name.asc(), project.name.asc(), part.name.asc());

//        QBean projection = Projections.bean(CustomerProjectPartDto.class,
//                customer.id.as(CustomerProjectPartDto.F_CUSTOMER_ID),
//                customer.name.as(CustomerProjectPartDto.F_CUSTOMER_NAME),
//                project.id.as(CustomerProjectPartDto.F_PROJECT_ID),
//                project.name.as(CustomerProjectPartDto.F_PROJECT_NAME),
//                part.id.as(CustomerProjectPartDto.F_PART_ID),
//                part.name.as(CustomerProjectPartDto.F_PART_NAME));

        Map<String,Expression<?>> bindings = new HashMap<>();
        bindings.put(CustomerProjectPartDto.F_CUSTOMER_ID, customer.id);
        bindings.put(CustomerProjectPartDto.F_CUSTOMER_NAME, customer.name);
        bindings.put(CustomerProjectPartDto.F_PROJECT_ID, project.id);
        bindings.put(CustomerProjectPartDto.F_PROJECT_NAME, project.name);
        bindings.put(CustomerProjectPartDto.F_PART_ID, part.id);
        bindings.put(CustomerProjectPartDto.F_PART_NAME, part.name);
        QBean projection = new QBean(CustomerProjectPartDto.class, true, bindings);

        return query.list(projection);
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
        }

        return list;
    }

    public static String formatDate(Date date) {
        return formatDate(date, "EEE dd. MMM. yyyy");
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }
}
