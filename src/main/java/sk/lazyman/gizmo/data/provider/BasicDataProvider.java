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

package sk.lazyman.gizmo.data.provider;

import com.mysema.query.types.Predicate;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author lazyman
 */
public class BasicDataProvider<T extends Serializable> extends SortableDataProvider<T, String> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicDataProvider.class);

    private JpaRepository<T, Integer> repository;
    private int itemsPerPage;

    private Predicate predicate;
    private Sort sort;

    public BasicDataProvider(JpaRepository<T, Integer> repository) {
        this(repository, 10);
    }

    public BasicDataProvider(JpaRepository<T, Integer> repository, int itemsPerPage) {
        this.repository = repository;
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        int pageIndex = (int) Math.ceil((double) first / (double) itemsPerPage);
        LOG.debug("Setting page request: page {}, size {}", pageIndex, itemsPerPage);

        Predicate predicate = getPredicate();
        PageRequest page = new PageRequest(pageIndex, itemsPerPage, sort);

        Page<T> found;
        if (predicate == null) {
            found = repository.findAll(page);
        } else {
            QueryDslPredicateExecutor executor = (QueryDslPredicateExecutor) repository;
            found = executor.findAll(predicate, page);
        }

        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<T>().iterator();
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public long size() {
        Predicate predicate = getPredicate();
        if (predicate == null) {
            return repository.count();
        }

        QueryDslPredicateExecutor executor = (QueryDslPredicateExecutor) repository;
        return executor.count(predicate);
    }

    @Override
    public IModel<T> model(T object) {
        return new Model(object);
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }
}
