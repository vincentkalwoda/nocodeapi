create table endpoint_parameters
(
    endpoint_id varchar(255) not null,
    parameter   varchar(255)
);

create table endpoints
(
    api_key         varchar(255) not null,
    description     TEXT,
    method          varchar(255) not null,
    route           varchar(255) not null,
    sql             TEXT         not null,
    project_api_key varchar(255) not null,
    primary key (api_key)
);

alter table if exists endpoint_parameters
    add constraint FKay2oadkiryeqq2m79y48v4nst
    foreign key (endpoint_id)
    references endpoints;

alter table if exists endpoints
    add constraint FKk1hmskhq2t9ky5bfmy4ym8ycm
    foreign key (project_api_key)
    references projects;
