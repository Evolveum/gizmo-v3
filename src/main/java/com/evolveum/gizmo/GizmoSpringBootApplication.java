/*
 * Copyright 2020 Katarina Valalikova
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
package com.evolveum.gizmo;

import jakarta.servlet.DispatcherType;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WicketFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestContextListener;

@EnableJpaRepositories("sk.lazyman.gizmo.repository")
@EnableTransactionManagement
@SpringBootApplication
public class GizmoSpringBootApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        try {
            SpringApplication.run(GizmoSpringBootApplication.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> requestContextListener() {
        return new ServletListenerRegistrationBean<>(new RequestContextListener());
    }

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilter(){
        FilterRegistrationBean<WicketFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new WicketFilter());
        filter.setDispatcherTypes(DispatcherType.ERROR, DispatcherType.REQUEST, DispatcherType.FORWARD);
        filter.addUrlPatterns("/*");
        filter.addInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        filter.addInitParameter(Application.CONFIGURATION, "deployment");     // deployment development
        filter.addInitParameter("applicationBean", "gizmoApplication");
        filter.addInitParameter(WicketFilter.APP_FACT_PARAM, "org.apache.wicket.spring.SpringWebApplicationFactory");
        return filter;
    }


}
