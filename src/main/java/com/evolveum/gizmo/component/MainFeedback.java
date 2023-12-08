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

package com.evolveum.gizmo.component;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;

/**
 * @author lazyman
 */
public class MainFeedback extends org.apache.wicket.markup.html.panel.FeedbackPanel {

    public MainFeedback(String id) {
        super(id);
    }

    @Override
    protected String getCSSClass(FeedbackMessage message) {
        switch (message.getLevel()) {
            case FeedbackMessage.ERROR:
            case FeedbackMessage.FATAL:
                return "alert-danger";
            case FeedbackMessage.INFO:
                return "alert-info";
            case FeedbackMessage.SUCCESS:
                return "alert-success";
            case FeedbackMessage.WARNING:
            case FeedbackMessage.DEBUG:
            case FeedbackMessage.UNDEFINED:
            default:
                return "alert-warning";
        }
    }

    @Override
    protected FeedbackMessagesModel newFeedbackMessagesModel() {
        return super.newFeedbackMessagesModel();
    }

    @Override
    protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
        return super.newMessageDisplayComponent(id, message);
    }
}
