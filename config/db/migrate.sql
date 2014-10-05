insert into g_user (id,name,givenname,familyname,ldapdn)
    select id,username,firstname,lastname,ldapdn from users;

insert into g_customer (id,name,description,type)
    select id,name,description,0 from company;

insert into g_project (id,closed,commercial,description,name,customer_id)
    select id,closed,commercial,description,name,customer_id from project;

insert into g_part (id,name,project_id)
    select id,name,project_id from parts;

insert into g_email_log(id,fromdate,mailbcc,mailcc,mailto,sentdate,successful,summaryinvoice,summarywork,todate,sender_id,description)
  select id,fromdate,mailbcc,mailcc,mailto,sentdate,successful,summaryinvoice,summarywork,todate,sender,comment from emaillog;

insert into g_emaillog_user (emaillog_id,realizatorlist_id)
  select log_id,user_id from emaillog_realizator;

insert into g_emaillog_project (emaillog_id,projectlist_id)
  select log_id,project_id from emaillog_project;

alter table tasks alter column description type character varying(2500);

insert into g_work (id,date,description,invoicelength,worklength,part_id,realizator_id)
  select id,date,description,invoice,length,part_id,realizator_id from tasks;