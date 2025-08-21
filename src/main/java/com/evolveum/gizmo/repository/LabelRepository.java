package com.evolveum.gizmo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import com.evolveum.gizmo.data.LabelPart;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<LabelPart, Integer>, QuerydslPredicateExecutor<LabelPart> {
}
