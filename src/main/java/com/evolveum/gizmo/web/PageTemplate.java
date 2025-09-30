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

package com.evolveum.gizmo.web;

import com.evolveum.gizmo.repository.*;
import com.evolveum.gizmo.util.LabelService;
import org.apache.commons.lang3.Validate;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.core.env.Environment;

import jakarta.persistence.EntityManager;

/**
 * @author lazyman
 */
public class PageTemplate extends WebPage {

    private static final String ID_DEBUG_PANEL = "debugPanel";
    private static final String ID_TITLE = "title";

    @SpringBean
    private EntityManager entityManager;
    @SpringBean
    private CustomerRepository customerRepository;
    @SpringBean
    private UserRepository userRepository;
    @SpringBean
    private ProjectRepository projectRepository;
    @SpringBean
    private PartRepository projectPartRepository;
    @SpringBean
    private WorkRepository workRepository;
    @SpringBean
    private EmailLogRepository emailLogRepository;
    @SpringBean
    private LogRepository logRepository;
    @SpringBean
    private AbstractTaskRepository abstractTaskRepository;
    @SpringBean
    private Environment environment;
    @SpringBean
    private LabelRepository labelRepository;

//    @SpringBean
//    private LabelService labelService;


    public PageTemplate() {
        this(null);
    }

    public PageTemplate(PageParameters parameters) {
        super(parameters);

        Injector.get().inject(this);
        initLayout();
    }

    private void initLayout() {
        Label title = new Label(ID_TITLE, createPageTitleModel());
        title.setRenderBodyOnly(true);
        add(title);

        DebugBar debugPanel = new DebugBar(ID_DEBUG_PANEL);
        add(debugPanel);
    }

    public String translateString(String resourceKey, Object... objects) {
        return createStringResource(resourceKey, objects).getString();
    }

    public StringResourceModel createStringResource(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey, this)
            .setDefaultValue(resourceKey)
            .setParameters(objects);
    }

    public StringResourceModel createStringResource(Enum e) {
        String resourceKey = e.getDeclaringClass().getSimpleName() + "." + e.name();
        return createStringResource(resourceKey);
    }

    public static StringResourceModel createStringResourceStatic(String resourceKey, Object... objects) {
        return new StringResourceModel(resourceKey)
                .setDefaultValue(resourceKey)
                .setParameters(objects);
    }

    public static StringResourceModel createStringResourceStatic(Enum e) {
        String resourceKey = e.getDeclaringClass().getSimpleName() + "." + e.name();
        return createStringResourceStatic(resourceKey);
    }


    protected IModel<String> createPageTitleModel() {
        return createStringResource("page.title");
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public PartRepository getProjectPartRepository() {
        return projectPartRepository;
    }

    public WorkRepository getWorkRepository() {
        return workRepository;
    }

    public EmailLogRepository getEmailLogRepository() {
        return emailLogRepository;
    }

    public LogRepository getLogRepository() {
        return logRepository;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public AbstractTaskRepository getAbstractTaskRepository() {
        return abstractTaskRepository;
    }

    public LabelRepository getLabelPartRepository() {
        return labelRepository;
    }

    public String getPropertyValue(String name) {
        Validate.notEmpty(name, "Property name must not be null or empty.");
        return environment.getProperty(name);
    }

    /**
     * It's here only because of some IDEs - it's not properly filtering resources during maven build.
     * "describe" variable is not replaced.
     *
     * @return "unknown" instead of "git describe" for current build.
     */
    @Deprecated
    public String getDescribe() {
        return getString("GizmoApplication.projectVersionUnknown");
    }
}
