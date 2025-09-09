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

package com.evolveum.gizmo.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.evolveum.gizmo.data.Part;

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

    //@Query("SELECT p FROM Part p WHERE p.color IS NULL OR p.color = ''")
    //List<Part> findAllWithoutColor();

    @Query("""
      select distinct p
      from Part p
      left join fetch p.labels
      where p.id in :ids
    """)
    List<Part> findAllWithLabelsByIdIn(java.util.Collection<Integer> ids);

    @Query("""
      select distinct p
      from Part p
      left join fetch p.labels
      join p.project pr
      where pr.id in :projectIds
    """)
    List<Part> findAllWithLabelsByProjectIdIn(java.util.Collection<Integer> projectIds);

    @Query("""
          select distinct p
          from Part p
          left join fetch p.labels
          join p.project pr
          join pr.customer c
          where c.id in :customerIds
        """)
    List<Part> findAllWithLabelsByCustomerIdIn(java.util.Collection<Integer> customerIds);

    @Query("""
        select distinct concat(c.name, ' - ', pr.name, ' - ', p.name)
        from Part p
          join p.project pr
          join pr.customer c
          join p.labels l
        where l.id = :labelId
        order by concat(c.name, ' - ', pr.name, ' - ', p.name)
    """)
    List<String> findCustomerProjectPartStringsByLabelId(@Param("labelId") Long labelId);

    @Query("""
        select distinct p
        from Part p
        left join fetch p.labels
        where p.id in (
            select p2.id
            from Part p2
            join p2.labels l2
            where l2.id = :labelId
        )
    """)
    List<Part> findAllWithLabelsByLabelId(@Param("labelId") Long labelId);

    @Override
    @EntityGraph(attributePaths = "labels")
    Page<Part> findAll(Predicate predicate, Pageable pageable);
}




