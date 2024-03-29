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

package com.evolveum.gizmo.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;

/**
 * @author lazyman
 */
public class BookmarkableLabeledLink<T> extends BookmarkablePageLink<T> {

    private IModel<String> model;

    public BookmarkableLabeledLink(String id, IModel<String> model, Class pageClass) {
        super(id, pageClass);
        this.model = model;
    }

    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        String text = model.getObject();
        if (StringUtils.isNotEmpty(text)) {
            replaceComponentTagBody(markupStream, openTag, text);
            return;
        }

        super.onComponentTagBody(markupStream, openTag);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (tag.isOpenClose()) {
            tag.setType(XmlTag.TagType.OPEN);
        }
    }
}
