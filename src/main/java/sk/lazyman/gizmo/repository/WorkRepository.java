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

package sk.lazyman.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import sk.lazyman.gizmo.data.Work;

import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public interface WorkRepository extends JpaRepository<Work, Integer>, QueryDslPredicateExecutor<Work> {

    @Query("from Work w where w.date > :from and w.date <= :to")
    public List<Work> findTasks(@Param("from") Date from, @Param("to") Date to);

}
