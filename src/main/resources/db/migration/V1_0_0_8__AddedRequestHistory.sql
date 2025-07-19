create table requests
(
    api_key         varchar(255) not null,
    body            TEXT,
    created_at      varchar(255) not null,
    error_message   TEXT,
    headers         TEXT,
    ip_address      varchar(255) not null,
    method          varchar(255) not null check (method in ('GET', 'POST', 'PUT', 'DELETE')),
    path            varchar(255) not null,
    query_params    TEXT,
    response        TEXT,
    response_time   time(6)      not null,
    status_code     integer      not null,
    user_agent      varchar(255) not null,
    project_api_key varchar(255) not null,
    primary key (api_key)
);

alter table if exists requests
    add constraint FK7v1rdf84h81sa0x24jsa08oew
    foreign key (project_api_key)
    references projects
    on
delete
cascade;

create table request_queryparams
(
    request_api_key varchar(255) not null,
    key             varchar(255),
    value           varchar(255)
);

alter table if exists request_queryparams
    add constraint FKdigrvpgalkoai5afy9f6u9an6
    foreign key (request_api_key)
    references requests
    on
delete
cascade;