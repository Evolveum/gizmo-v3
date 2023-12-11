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

package com.evolveum.gizmo.component.data;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.markup.repeater.data.DataViewBase;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.util.LoadableModel;

/**
 * @author lazyman
 */
public class NavigatorToolbar extends Panel {

    private int PAGING_SIZE = 5;

    private static final String ID_PREVIOUS = "previous";
    private static final String ID_PREVIOUS_LINK = "previousLink";
    private static final String ID_FIRST = "first";
    private static final String ID_FIRST_LINK = "firstLink";
    private static final String ID_DOTS = "dots";
    private static final String ID_NAVIGATION = "navigation";
    private static final String ID_PAGE_LINK = "pageLink";
    private static final String ID_NEXT = "next";
    private static final String ID_NEXT_LINK = "nextLink";

    private static final String ID_TD = "td";
    private static final String ID_COUNT = "count";


    private final boolean showPageListing;
    private DataTable<?, ?> table;

    public NavigatorToolbar(String id, DataTable<?, ?> table, boolean showPageListing) {
        super(id);
        this.table = table;
        this.showPageListing = showPageListing;

        setOutputMarkupId(true);
        add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return NavigatorToolbar.this.getTable().getPageCount() > 0;
            }
        });
    }

    public DataTable<?, ?> getTable() {
        return table;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initLayout();
    }

    private void initLayout() {
        initNavigationPanel();
        initCountPanel();
    }

    private void initNavigationPanel() {
        initPrevious();
        initFirst();
        initNavigation();
        initNext();
    }

    private void initCountPanel() {
//        WebMarkupContainer td = new WebMarkupContainer(ID_TD);
//        td.add(AttributeModifier.replace("colspan", new IModel<String>() {
//
//            @Override
//            public String getObject() {
//                return String.valueOf(getTable().getColumns().size());
//            }
//        }));
//        add(td);

        Label count = new Label(ID_COUNT, createModel());
        count.setRenderBodyOnly(true);
        add(count);

    }

    private IModel<String> createModel() {
        return new LoadableModel<String>() {

            @Override
            protected String load() {
                long from = 0;
                long to = 0;
                long count = 0;

                IPageable pageable = getTable();
                if (pageable instanceof DataViewBase) {
                    DataViewBase view = (DataViewBase) pageable;

                    from = view.getFirstItemOffset() + 1;
                    to = from + view.getItemsPerPage() - 1;
                    long itemCount = view.getItemCount();
                    if (to > itemCount) {
                        to = itemCount;
                    }
                    count = itemCount;
                } else if (pageable instanceof DataTable) {
                    DataTable table = (DataTable) pageable;

                    from = table.getCurrentPage() * table.getItemsPerPage() + 1;
                    to = from + table.getItemsPerPage() - 1;
                    long itemCount = table.getItemCount();
                    if (to > itemCount) {
                        to = itemCount;
                    }
                    count = itemCount;
                }

                if (count > 0) {
                    return new StringResourceModel("CountToolbar.label", NavigatorToolbar.this)
                            .setParameters(new Object[]{from, to, count}).getString();
                }

                return new StringResourceModel("CountToolbar.noFound", NavigatorToolbar.this, null).getString();
            }
        };
    }

    private void initPrevious() {
        WebMarkupContainer previous = new WebMarkupContainer(ID_PREVIOUS);
        previous.add(new AttributeAppender("class", new IModel<String>() {

            @Override
            public String getObject() {
                return isPreviousEnabled() ? "" : " disabled";
            }
        }));
        add(previous);
        AjaxLink<Void> previousLink = new AjaxLink<>(ID_PREVIOUS_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousPerformed(target);
            }
        };
        previousLink.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isEnabled() {
                return isPreviousEnabled();
            }
        });
        previous.add(previousLink);
    }

    private void initFirst() {
        WebMarkupContainer first = new WebMarkupContainer(ID_FIRST);
        first.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return showPageListing && showFirstAndDots();
            }
        });
        add(first);
        AjaxLink<Void> firstLink = new AjaxLink<>(ID_FIRST_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                firstPerformed(target);
            }
        };
        first.add(firstLink);

        WebMarkupContainer dots = new WebMarkupContainer(ID_DOTS);
        dots.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return showPageListing && showFirstAndDots();
            }
        });
        add(dots);
    }

    private void initNavigation() {
        IModel<Integer> model = () -> {
            int count = (int) getTable().getPageCount();
            if (count < PAGING_SIZE) {
                return count;
            }

            return PAGING_SIZE;
        };

        Loop navigation = new Loop(ID_NAVIGATION, model) {

            @Override
            protected void populateItem(final LoopItem item) {
                final NavigatorPageLink pageLink = new NavigatorPageLink(ID_PAGE_LINK,
                        computePageNumber(item.getIndex())) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        pageLinkPerformed(target, getPageNumber());
                    }
                };
                item.add(pageLink);

                item.add(new AttributeAppender("class", (IModel<String>) () -> getTable().getCurrentPage() == pageLink.getPageNumber() ? " active" : ""));
            }
        };
        navigation.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return showPageListing;
            }
        });
        add(navigation);
    }

    private long computePageNumber(int loopIndex) {
        long current = getTable().getCurrentPage();
        long count = getTable().getPageCount();

        final long half = PAGING_SIZE / 2;

        long result;
        if (current - half <= 0) {
            result = loopIndex;
        } else if (current + half + 1 >= count) {
            result = count - PAGING_SIZE + loopIndex;
        } else {
            result = current - half + loopIndex;
        }

        if (count == 4 && current == 3) {
            result++;
        }

        return result;
    }

    private void initNext() {
        WebMarkupContainer next = new WebMarkupContainer(ID_NEXT);
        next.add(new AttributeAppender("class", new IModel<String>() {

            @Override
            public String getObject() {
                return isNextEnabled() ? "" : " disabled";
            }
        }));
        add(next);

        AjaxLink<Void> nextLink = new AjaxLink<Void>(ID_NEXT_LINK) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                nextPerformed(target);
            }
        };
        nextLink.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isEnabled() {
                return isNextEnabled();
            }
        });
        next.add(nextLink);
    }

    private boolean isPreviousEnabled() {
        return getTable().getCurrentPage() > 0;
    }

    private boolean isNextEnabled() {
        return getTable().getCurrentPage() + 1 < getTable().getPageCount();
    }

    private boolean showFirstAndDots() {
        return getTable().getCurrentPage() >= PAGING_SIZE - 1;
    }

    private void previousPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, getTable().getCurrentPage() - 1);
    }

    private void firstPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, 0);
    }

    private void nextPerformed(AjaxRequestTarget target) {
        changeCurrentPage(target, getTable().getCurrentPage() + 1);
    }

    private void changeCurrentPage(AjaxRequestTarget target, long page) {

            // Tell the PageableListView which page to print next
            getTable().setCurrentPage(page);

            target.add(getTable());
            target.add(this);
            // Return the current page.
//            setResponsePage(getPage());
//        }
//        getTable().setCurrentPage(page);
//
//        Component container = getTable();
//        while (container instanceof AbstractRepeater) {
//            container = container.getParent();
//        }
//        target.add(container);
////        target.add(this);
    }

    private void pageLinkPerformed(AjaxRequestTarget target, long page) {
        changeCurrentPage(target, page);
    }
}
