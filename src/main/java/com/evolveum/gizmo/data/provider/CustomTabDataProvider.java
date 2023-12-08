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

package com.evolveum.gizmo.data.provider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author lazyman
 */
public class CustomTabDataProvider<T extends Serializable> extends BasicDataProvider<T> {

    public CustomTabDataProvider(JpaRepository<T, Integer> repository) {
        super(repository);
    }

    public CustomTabDataProvider(JpaRepository<T, Integer> repository, int itemsPerPage) {
        super(repository, itemsPerPage);
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        if (getPredicate() == null) {
            return new ArrayList<T>().iterator();
        }
        return super.iterator(first, count);
    }

    @Override
    public long size() {
        if (getPredicate() == null) {
            return 0L;
        }
        return super.size();
    }
}
