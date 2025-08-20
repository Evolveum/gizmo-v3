package com.evolveum.gizmo.util;

import com.evolveum.gizmo.data.LabelPart;
import com.evolveum.gizmo.data.QLabelPart;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class LabelService {
    @PersistenceContext private EntityManager em;
    public List<LabelPart> findAllOrdered() {
        QLabelPart l = QLabelPart.labelPart;
        return new JPAQuery<LabelPart>(em)
                .select(l).from(l).orderBy(l.code.asc()).fetch();
    }
}