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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.QEmailLog;
import sk.lazyman.gizmo.dto.EmailFilterDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class EmailDataProvider extends BasicDataProvider<EmailLog> {

    private EmailFilterDto filter;

    public EmailDataProvider(JpaRepository<EmailLog, Integer> repository, int itemsPerPage) {
        super(repository, itemsPerPage);
    }

    public void setFilter(EmailFilterDto filter) {
        this.filter = filter;
    }

    @Override
    public Predicate getPredicate() {
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        if (filter.getFrom() != null) {
            list.add(QEmailLog.emailLog.sentDate.goe(filter.getFrom()));
        }

        if (filter.getTo() != null) {
            list.add(QEmailLog.emailLog.sentDate.loe(filter.getTo()));
        }

        if (filter.getSender() != null) {
            list.add(QEmailLog.emailLog.sender.eq(filter.getSender()));
        }

        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }
}
