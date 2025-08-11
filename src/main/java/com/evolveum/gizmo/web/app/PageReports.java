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

package com.evolveum.gizmo.web.app;

import com.evolveum.gizmo.component.*;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;

import org.wicketstuff.annotation.mount.MountPath;

import java.util.ArrayList;
import java.util.List;

@MountPath("/app/reports")
public class PageReports extends PageAppTemplate {

    private static final String ID_TABS = "tabs";

    public PageReports(){
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    private void initLayout() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(createStringResource("PageReports.tab.overview")) {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ReportOverviewTab(panelId);
            }
        });

        tabs.add(new AbstractTab(createStringResource("PageReports.tab.usersummary")) {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ReportTimeoffTab(panelId);
            }
        });

        tabs.add(new AbstractTab(createStringResource("PageReports.tab.partsummary")) {
            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new ReportSalesTab(panelId);
            }
        });
        GizmoTabbedPanel<ITab> tabbedPanel = new GizmoTabbedPanel<>(ID_TABS, tabs);
        tabbedPanel.setOutputMarkupId(true);
        add(tabbedPanel);
    }
}