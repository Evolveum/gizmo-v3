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

package com.evolveum.gizmo.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author lazyman
 */
public class GizmoNamingStrategy implements PhysicalNamingStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(GizmoNamingStrategy.class);

//    protected String transformName(EntityNaming entityNaming) {
//        String className = super.transformEntityName(entityNaming);
//        String result = "g_" + className.toLowerCase();
//        LOG.trace("classToTableName {} to {}", new Object[]{className, result});
//
//        return result;
//    }

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
//        return transformIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
//        return transformIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return transformIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
//        return transformIdentifier(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return identifier;
//        return transformIdentifier(identifier);
    }
//
//    @Override
    public Identifier transformIdentifier(Identifier identifier) {
        if (identifier == null) {
            return identifier;
        }
        String className = identifier.getText();
        //change camel case to underscore delimited
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        className = className.replaceAll(regex, replacement)
                .toLowerCase();
//        className = className.replaceAll(String.format("%s|%s|%s",
//                "(?<=[A-Z])(?=[A-Z][a-z])",
//                "(?<=[^A-Z])(?=[A-Z])",
//                "(?<=[A-Za-z])(?=[^A-Za-z])"
//        ), "_");

        String result = "g_" + className.toLowerCase();
        LOG.trace("classToTableName {} to {}", new Object[]{className, result});

        return Identifier.toIdentifier(result);
    }

//    tran
//
//    @Override
//    public String tableName(String tableName) {
//        String result = "g_" + super.tableName(tableName);
//        LOG.trace("tableName {} to {}", new Object[]{tableName, result});
//
//        return result;
//    }
}
