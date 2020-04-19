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
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.lazyman.gizmo.data.Part;

import java.util.List;

/**
 * @author lazyman
 */
@Repository
public interface PartRepository extends JpaRepository<Part, Integer>, QuerydslPredicateExecutor<Part> {

    @Query("from Part p where p.project.id = :projectId order by p.name")
    public List<Part> findParts(@Param("projectId") Integer projectId);

    @Query("from Part p where p.project.closed = false")
    List<Part> findOpenedProjectParts();
}
