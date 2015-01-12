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

package sk.lazyman.gizmo.security;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.less.BootstrapLess;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import sk.lazyman.gizmo.theme.GizmoThemeProvider;
import sk.lazyman.gizmo.web.PageLogin;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.app.PageAppTemplate;
import sk.lazyman.gizmo.web.app.PageDashboard;
import sk.lazyman.gizmo.web.error.PageError;
import sk.lazyman.gizmo.web.error.PageError401;
import sk.lazyman.gizmo.web.error.PageError403;
import sk.lazyman.gizmo.web.error.PageError404;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lazyman
 */
@Component("gizmoApplication")
public class GizmoApplication extends AuthenticatedWebApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(GizmoApplication.class);

    @Override
    public Class<PageDashboard> getHomePage() {
        return PageDashboard.class;
    }

    @Override
    public void init() {
        super.init();

        IBootstrapSettings settings = new BootstrapSettings();
        settings.setAutoAppendResources(false);
        settings.setThemeProvider(new GizmoThemeProvider());
        Bootstrap.install(this, settings);
        BootstrapLess.install(this);

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        IResourceSettings resourceSettings = getResourceSettings();

        resourceSettings.setThrowExceptionOnMissingResource(false);
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setDefaultBeforeDisabledLink("");
        getMarkupSettings().setDefaultAfterDisabledLink("");

        if (RuntimeConfigurationType.DEVELOPMENT.equals(getConfigurationType())) {
            getDebugSettings().setAjaxDebugModeEnabled(true);
            getDebugSettings().setDevelopmentUtilitiesEnabled(true);
        }

        //exception handling an error pages
        IApplicationSettings appSettings = getApplicationSettings();
        appSettings.setAccessDeniedPage(PageError401.class);
        appSettings.setInternalErrorPage(PageError.class);
        appSettings.setPageExpiredErrorPage(PageError.class);

        new AnnotatedMountScanner().scanPackage(PageTemplate.class.getPackage().getName()).mount(this);

        mount(new MountedMapper("/error", PageError.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/401", PageError401.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/403", PageError403.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/404", PageError404.class, new UrlPathPageParametersEncoder()));

        getRequestCycleListeners().add(new AbstractRequestCycleListener() {

            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                return new RenderPageRequestHandler(new PageProvider(new PageError(ex)));
            }
        });
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return PageLogin.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return GizmoAuthWebSession.class;
    }
}
