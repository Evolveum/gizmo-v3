# Gizmo database migration and upgrade manual

- **Migrate** from an old database schema
- **Upgrade** of an already migrated database to include new columns or features
---
## Backup
Make full backup of your current database:
```bash
pg_dump -C -b gizmo > 01-gizmo_backup.sql
```
Ensure you have access to the sql scripts located in:
```bash
config/db/
```


## Migration
1. Connect to the database
```bash
   psql -U gizmo_user -d gizmo
```
2. Run migration script
```bash
   \i config/db/migrate.sql.manual
```
3. Drop old tables
```bash
   \i config/db/drop-old.sql.manual
```
## Upgrade
1. Run the upgrade script
```bash
   psql -U gizmo_user -d gizmo -f config/db/02-upgrade.sql
```
2. Update your configuration file
```bash
   <tomcat>/config/gizmo.properties
```
3. Deploy the new .war file to Tomcat

