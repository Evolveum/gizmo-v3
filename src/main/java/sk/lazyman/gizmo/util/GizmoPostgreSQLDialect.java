package sk.lazyman.gizmo.util;

import org.hibernate.dialect.PostgresPlusDialect;

import java.sql.Types;

/**
 * @author lazyman
 */
public class GizmoPostgreSQLDialect extends PostgresPlusDialect {

    public GizmoPostgreSQLDialect() {
        super();

        registerColumnType( Types.DOUBLE, "float4" );
    }
}
