CREATE TABLE AbstractTask (
  id            INT4   NOT NULL,
  date          TIMESTAMP,
  description   VARCHAR(3000),
  trackId       VARCHAR(255),
  workLength    FLOAT4 NOT NULL,
  realizator_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Attachment (
  id          INT4 NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255),
  value       BYTEA,
  log_id      INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Contact (
  id          INT4 NOT NULL,
  city        VARCHAR(255),
  country     VARCHAR(255),
  description VARCHAR(255),
  name        VARCHAR(255),
  street      VARCHAR(255),
  zip         VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE ContactValue (
  id         INT4 NOT NULL,
  type       INT4,
  value      VARCHAR(255),
  contact_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Customer (
  id          INT4 NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255),
  type        INT4,
  partner_id  INT4,
  PRIMARY KEY (id)
);

CREATE TABLE EmailLog (
  id             INT4    NOT NULL,
  description    VARCHAR(3000),
  fromDate       TIMESTAMP,
  mailBcc        VARCHAR(255),
  mailCc         VARCHAR(255),
  mailTo         VARCHAR(255),
  sentDate       TIMESTAMP,
  successful     BOOLEAN NOT NULL,
  summaryInvoice FLOAT4  NOT NULL,
  summaryWork    FLOAT4  NOT NULL,
  toDate         TIMESTAMP,
  sender_id      INT4,
  PRIMARY KEY (id)
);

CREATE TABLE EmailLog_Project (
  EmailLog_id    INT4 NOT NULL,
  projectList_id INT4 NOT NULL,
  PRIMARY KEY (EmailLog_id, projectList_id)
);

CREATE TABLE EmailLog_User (
  EmailLog_id       INT4 NOT NULL,
  realizatorList_id INT4 NOT NULL,
  PRIMARY KEY (EmailLog_id, realizatorList_id)
);

CREATE TABLE Log (
  id          INT4 NOT NULL,
  customer_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Notification (
  id          INT4      NOT NULL,
  alarm       TIMESTAMP NOT NULL,
  created     TIMESTAMP NOT NULL,
  description VARCHAR(3000),
  owner       BYTEA     NOT NULL,
  customer_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Notification_emails (
  Notification_id INT4 NOT NULL,
  emails          VARCHAR(255)
);

CREATE TABLE Part (
  id          INT4 NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255),
  project_id  INT4,
  PRIMARY KEY (id)
);

CREATE TABLE Project (
  id          INT4    NOT NULL,
  closed      BOOLEAN NOT NULL,
  commercial  BOOLEAN NOT NULL,
  description VARCHAR(3000),
  name        VARCHAR(255),
  customer_id INT4,
  PRIMARY KEY (id)
);

CREATE TABLE User (
  id         INT4    NOT NULL,
  enabled    BOOLEAN NOT NULL,
  familyName VARCHAR(255),
  givenName  VARCHAR(255),
  ldapDn     VARCHAR(255),
  name       VARCHAR(255),
  password   VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE Work (
  invoiceLength FLOAT4 NOT NULL,
  id            INT4   NOT NULL,
  part_id       INT4,
  PRIMARY KEY (id)
);

ALTER TABLE User
ADD CONSTRAINT u_ldapdn UNIQUE (ldapDn);

ALTER TABLE User
ADD CONSTRAINT u_name UNIQUE (name);

ALTER TABLE AbstractTask
ADD CONSTRAINT fk_abstractTask_user
FOREIGN KEY (realizator_id)
REFERENCES User;

ALTER TABLE Attachment
ADD CONSTRAINT fk_attachment_log
FOREIGN KEY (log_id)
REFERENCES Log;

ALTER TABLE ContactValue
ADD CONSTRAINT fk_contactValue_contact
FOREIGN KEY (contact_id)
REFERENCES Contact;

ALTER TABLE Customer
ADD CONSTRAINT fk_customer_customer
FOREIGN KEY (partner_id)
REFERENCES Customer;

ALTER TABLE EmailLog
ADD CONSTRAINT fk_emaillog_user
FOREIGN KEY (sender_id)
REFERENCES User;

ALTER TABLE EmailLog_Project
ADD CONSTRAINT FK_5h11y4q3hi94tmxry29rj9yt3
FOREIGN KEY (projectList_id)
REFERENCES Project;

ALTER TABLE EmailLog_Project
ADD CONSTRAINT FK_bl6g2d1uxcjoy6an283bj8b15
FOREIGN KEY (EmailLog_id)
REFERENCES EmailLog;

ALTER TABLE EmailLog_User
ADD CONSTRAINT FK_3m3u8yasqwvowwumuaripd57c
FOREIGN KEY (realizatorList_id)
REFERENCES User;

ALTER TABLE EmailLog_User
ADD CONSTRAINT FK_adwogr4sj4o6ktojlse2yko5p
FOREIGN KEY (EmailLog_id)
REFERENCES EmailLog;

ALTER TABLE Log
ADD CONSTRAINT fk_log_customer
FOREIGN KEY (customer_id)
REFERENCES Customer;

ALTER TABLE Log
ADD CONSTRAINT FK_4hrj1flwhdimpvxwmvtf6534g
FOREIGN KEY (id)
REFERENCES AbstractTask;

ALTER TABLE Notification
ADD CONSTRAINT fk_notification_customer
FOREIGN KEY (customer_id)
REFERENCES Customer;

ALTER TABLE Notification_emails
ADD CONSTRAINT FK_d6o6s37g3nro1602635phkaev
FOREIGN KEY (Notification_id)
REFERENCES Notification;

ALTER TABLE Part
ADD CONSTRAINT FK_h8fjfbo2kl07u8snpis7poimx
FOREIGN KEY (project_id)
REFERENCES Project;

ALTER TABLE Project
ADD CONSTRAINT fk_project_customer
FOREIGN KEY (customer_id)
REFERENCES Customer;

ALTER TABLE Work
ADD CONSTRAINT fk_work_part
FOREIGN KEY (part_id)
REFERENCES Part;

ALTER TABLE Work
ADD CONSTRAINT FK_9felfxkau9dqjbvpux357btt0
FOREIGN KEY (id)
REFERENCES AbstractTask;

CREATE SEQUENCE g_attachment_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_contact_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_contact_value_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_customer_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_email_log_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_notification_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_part_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_project_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_task_id_seq START 1 INCREMENT 50;

CREATE SEQUENCE g_user_id_seq START 1 INCREMENT 50;