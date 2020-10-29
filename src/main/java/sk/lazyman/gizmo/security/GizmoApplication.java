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
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.settings.ApplicationSettings;
import org.apache.wicket.settings.ResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;
import sk.lazyman.gizmo.web.PageLogin;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.app.PageDashboard;
import sk.lazyman.gizmo.web.error.PageError;
import sk.lazyman.gizmo.web.error.PageError401;
import sk.lazyman.gizmo.web.error.PageError403;
import sk.lazyman.gizmo.web.error.PageError404;

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

//        Map<String, GizmoApplicationConfiguration> map =
//                applicationContext.getBeansOfType(GizmoApplicationConfiguration.class);
//        if (map != null) {
//            map.forEach((key, value) -> value.init(this));
//        }

//        IBootstrapSettings settings = new BootstrapSettings();
//        settings.setAutoAppendResources(false);
//        settings.useCdnResources(false);
//        settings.setThemeProvider(new GizmoThemeProvider());
//        Bootstrap.install(this, settings);
//        BootstrapLess.install(this);

        getJavaScriptLibrarySettings().setJQueryReference(
                new PackageResourceReference(GizmoApplication.class,
                        "../../../../webjars/AdminLTE/3.0.5/plugins/jquery/jquery.min.js"));


        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        ResourceSettings resourceSettings = getResourceSettings();

        resourceSettings.setThrowExceptionOnMissingResource(false);
        getMarkupSettings().setStripWicketTags(true);
//        getMarkupSettings().setDefaultBeforeDisabledLink("");
//        getMarkupSettings().setDefaultAfterDisabledLink("");

        if (RuntimeConfigurationType.DEVELOPMENT.equals(getConfigurationType())) {
            getDebugSettings().setAjaxDebugModeEnabled(true);
            getDebugSettings().setDevelopmentUtilitiesEnabled(true);
        }

        //exception handling an error pages
        ApplicationSettings appSettings = getApplicationSettings();
        appSettings.setAccessDeniedPage(PageError401.class);
        appSettings.setInternalErrorPage(PageError.class);
        appSettings.setPageExpiredErrorPage(PageError.class);

        new AnnotatedMountScanner().scanPackage(PageTemplate.class.getPackage().getName()).mount(this);
//        mountResource("css/font-awesome.css", FontAwesomeCssReference.instance());

        mount(new MountedMapper("/error", PageError.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/401", PageError401.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/403", PageError403.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/404", PageError404.class, new UrlPathPageParametersEncoder()));

//        getRequestCycleListeners().add(new CsrfPreventionRequestCycleListener());
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {

            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                LOGGER.error("Error occurred during page rendering, reason: {} (more on DEBUG level)", ex.getMessage());
                LOGGER.debug("Error occurred during page rendering", ex);

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
