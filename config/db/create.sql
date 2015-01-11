CREATE DATABASE gizmo3 WITH OWNER = gizmo
  ENCODING = 'UTF8'
  TABLESPACE = pg_default
  LC_COLLATE = 'en_US.UTF-8'
  LC_CTYPE = 'en_US.UTF-8'
  CONNECTION LIMIT = -1;

COMMENT ON DATABASE gizmo3 IS 'gizmo v3 database';

CREATE TABLE g_EmailLog_Customer (
  EmailLog_id     INT4 NOT NULL,
  customerList_id INT4 NOT NULL,
  PRIMARY KEY (EmailLog_id, customerList_id)
);

CREATE TABLE g_EmailLog_Project (
  EmailLog_id    INT4 NOT NULL,
  projectList_id INT4 NOT NULL,
  PRIMARY KEY (EmailLog_id, projectList_id)
);

CREATE TABLE g_EmailLog_User (
  log_id  INT4 NOT NULL,
  user_id INT4 NOT NULL,
  PRIMARY KEY (log_id, user_id)
);

CREATE TABLE g_Notification_emails (
  Notification_id INT4 NOT NULL,
  emails          VARCHAR(255)
);

CREATE TABLE g_abstract_task (
  id            INT4      NOT NULL,
  date          TIMESTAMP NOT NULL,
  description   VARCHAR(3000),
  trackId       VARCHAR(255),
  type          INT4      NOT NULL,
  workLength    FLOAT4    NOT NULL,
  realizator_id INT4      NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_attachment (
  id          INT4         NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255) NOT NULL,
  value       BYTEA,
  log_id      INT4         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_contact (
  id          INT4         NOT NULL,
  city        VARCHAR(255),
  country     VARCHAR(255),
  description VARCHAR(255),
  name        VARCHAR(255) NOT NULL,
  street      VARCHAR(255),
  zip         VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE g_contact_value (
  id         INT4         NOT NULL,
  type       INT4,
  value      VARCHAR(255) NOT NULL,
  contact_id INT4         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_customer (
  id          INT4         NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255) NOT NULL,
  type        INT4,
  partner_id  INT4,
  PRIMARY KEY (id)
);

CREATE TABLE g_email_log (
  id             INT4      NOT NULL,
  description    VARCHAR(3000),
  fromDate       TIMESTAMP,
  mailBcc        VARCHAR(255),
  mailCc         VARCHAR(255),
  mailTo         VARCHAR(255),
  sentDate       TIMESTAMP NOT NULL,
  successful     BOOLEAN   NOT NULL,
  summaryInvoice FLOAT4    NOT NULL,
  summaryWork    FLOAT4    NOT NULL,
  toDate         TIMESTAMP,
  sender_id      INT4      NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_log (
  id          INT4 NOT NULL,
  customer_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE g_notification (
  id          INT4      NOT NULL,
  alarm       TIMESTAMP NOT NULL,
  created     TIMESTAMP NOT NULL,
  description VARCHAR(3000),
  customer_id INT4      NOT NULL,
  owner_id    INT4      NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_part (
  id          INT4         NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255) NOT NULL,
  project_id  INT4         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_project (
  id          INT4         NOT NULL,
  closed      BOOLEAN      NOT NULL,
  commercial  BOOLEAN      NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255) NOT NULL,
  customer_id INT4         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE g_user (
  id         INT4         NOT NULL,
  enabled    BOOLEAN      NOT NULL,
  familyName VARCHAR(255),
  givenName  VARCHAR(255),
  ldapDn     VARCHAR(255),
  name       VARCHAR(255) NOT NULL,
  password   VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE g_work (
  invoiceLength FLOAT4 NOT NULL,
  id            INT4   NOT NULL,
  part_id       INT4   NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE g_user
ADD CONSTRAINT u_ldapdn UNIQUE (ldapDn);

ALTER TABLE g_user
ADD CONSTRAINT u_name UNIQUE (name);

ALTER TABLE g_EmailLog_Customer
ADD CONSTRAINT FK_g23s45xg3a3hbtmhd9s3bou8g
FOREIGN KEY (customerList_id)
REFERENCES g_customer;

ALTER TABLE g_EmailLog_Customer
ADD CONSTRAINT FK_d428p4two6kc3ph7qwwwg7mdx
FOREIGN KEY (EmailLog_id)
REFERENCES g_email_log;

ALTER TABLE g_EmailLog_Project
ADD CONSTRAINT FK_cekxbqvdbixd13gxa3qslkafj
FOREIGN KEY (projectList_id)
REFERENCES g_project;

ALTER TABLE g_EmailLog_Project
ADD CONSTRAINT FK_oi1upgvklwxnasqb43y4wuq4l
FOREIGN KEY (EmailLog_id)
REFERENCES g_email_log;

ALTER TABLE g_EmailLog_User
ADD CONSTRAINT FK_dubkv2pfantss2ch9msogdjr9
FOREIGN KEY (user_id)
REFERENCES g_user;

ALTER TABLE g_EmailLog_User
ADD CONSTRAINT FK_joe1pwy6xoxewqp2pgxcx3l76
FOREIGN KEY (log_id)
REFERENCES g_email_log;

ALTER TABLE g_Notification_emails
ADD CONSTRAINT FK_jr84n71h4x166rtr0nwkaqug8
FOREIGN KEY (Notification_id)
REFERENCES g_notification;

ALTER TABLE g_abstract_task
ADD CONSTRAINT fk_abstractTask_user
FOREIGN KEY (realizator_id)
REFERENCES g_user;

ALTER TABLE g_attachment
ADD CONSTRAINT fk_attachment_log
FOREIGN KEY (log_id)
REFERENCES g_log;

ALTER TABLE g_contact_value
ADD CONSTRAINT fk_contactValue_contact
FOREIGN KEY (contact_id)
REFERENCES g_contact;

ALTER TABLE g_customer
ADD CONSTRAINT fk_customer_customer
FOREIGN KEY (partner_id)
REFERENCES g_customer;

ALTER TABLE g_email_log
ADD CONSTRAINT fk_emaillog_user
FOREIGN KEY (sender_id)
REFERENCES g_user;

ALTER TABLE g_log
ADD CONSTRAINT fk_log_customer
FOREIGN KEY (customer_id)
REFERENCES g_customer;

ALTER TABLE g_log
ADD CONSTRAINT FK_6m8l0gkoe4hubqjwmj9vw9x5x
FOREIGN KEY (id)
REFERENCES g_abstract_task;

ALTER TABLE g_notification
ADD CONSTRAINT fk_notification_customer
FOREIGN KEY (customer_id)
REFERENCES g_customer;

ALTER TABLE g_notification
ADD CONSTRAINT fk_notification_owner
FOREIGN KEY (owner_id)
REFERENCES g_user;

ALTER TABLE g_part
ADD CONSTRAINT FK_hbetssupir7kry5d3ik0209np
FOREIGN KEY (project_id)
REFERENCES g_project;

ALTER TABLE g_project
ADD CONSTRAINT fk_project_customer
FOREIGN KEY (customer_id)
REFERENCES g_customer;

ALTER TABLE g_work
ADD CONSTRAINT fk_work_part
FOREIGN KEY (part_id)
REFERENCES g_part;

ALTER TABLE g_work
ADD CONSTRAINT FK_isskwp31rcqe9lppoo5l1earh
FOREIGN KEY (id)
REFERENCES g_abstract_task;

CREATE SEQUENCE g_attachment_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_contact_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_contact_value_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_customer_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_email_log_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_notification_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_part_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_project_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_task_id_seq START 40000 INCREMENT 1;

CREATE SEQUENCE g_user_id_seq START 40000 INCREMENT 1;
