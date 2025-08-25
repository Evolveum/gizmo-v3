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

package com.evolveum.gizmo.security;

import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.web.PageLogin;
import com.evolveum.gizmo.web.PageTemplate;
import com.evolveum.gizmo.web.app.PageDashboard;
import com.evolveum.gizmo.web.error.PageError;
import com.evolveum.gizmo.web.error.PageError401;
import com.evolveum.gizmo.web.error.PageError403;
import com.evolveum.gizmo.web.error.PageError404;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.request.mapper.MountedMapper;
import org.apache.wicket.csp.CSPDirective;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.settings.ApplicationSettings;
import org.apache.wicket.settings.ResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.wicketstuff.annotation.scan.AnnotatedMountScanner;

/**
 * @author lazyman
 */
@Component("gizmoApplication")
public class GizmoApplication extends AuthenticatedWebApplication {

    @Override
    public Class<PageDashboard> getHomePage() {
        return PageDashboard.class;
    }

    @Override
    public void init() {
        super.init();

        getCspSettings().blocking().clear()
                .unsafeInline()
                .add(CSPDirective.IMG_SRC, "data:")
                .add(CSPDirective.FONT_SRC, "data:");

        getJavaScriptLibrarySettings().setJQueryReference(
                new PackageResourceReference(GizmoApplication.class, "../../../../META-INF/resources/webjars/jquery/3.6.0/jquery.min.js"));

        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        ResourceSettings resourceSettings = getResourceSettings();

        resourceSettings.setThrowExceptionOnMissingResource(false);
        getMarkupSettings().setStripWicketTags(true);

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

        mount(new MountedMapper("/error", PageError.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/401", PageError401.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/403", PageError403.class, new UrlPathPageParametersEncoder()));
        mount(new MountedMapper("/error/404", PageError404.class, new UrlPathPageParametersEncoder()));

        getAjaxRequestTargetListeners().add(new AjaxRequestTarget.IListener() {

            @Override
            public void updateAjaxAttributes(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {
                // check whether behavior will use POST method, if not then don't put CSRF token there
                if (!isPostMethodTypeBehavior(behavior, attributes)) {
                    return;
                }

                CsrfToken csrfToken = GizmoUtils.getCsrfToken();
                if (csrfToken == null) {
                    return;
                }

                String parameterName = csrfToken.getParameterName();
                String value = csrfToken.getToken();

                attributes.getExtraParameters().put(parameterName, value);

            }

        });
    }

    private boolean isPostMethodTypeBehavior(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {
        if (behavior instanceof AjaxFormComponentUpdatingBehavior) {
            // it also uses POST, but they set it after this method is called
            return true;
        }

        if (behavior instanceof AjaxFormSubmitBehavior fb) {
            Form<?> form = fb.getForm();
            String formMethod = form.getMarkupAttributes().getString("method");
            if (formMethod == null || "POST".equalsIgnoreCase(formMethod) || form.getRootForm().isMultiPart()) {
                // this will also use POST
                return true;
            }
        }

        return AjaxRequestAttributes.Method.POST.equals(attributes.getMethod());
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
