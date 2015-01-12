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
import sk.lazyman.gizmo.data.Customer;

import java.util.List;

/**
 * @author lazyman
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer>, QueryDslPredicateExecutor<Customer> {

    @Query("from Customer c order by c.name")
    public List<Customer> listCustomers();

    @Query("from Customer c where c.type = 2 order by c.name")
    public List<Customer> listPartners();

    @Query("select distinct c from Customer c left join c.projects p where p.closed != false")
    public List<Customer> listCustomersWithOpenProjects();
}
