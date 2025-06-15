create table api_access_tokens
(
    api_key         varchar(255) not null,
    created_at      timestamp(6) not null,
    expires_at      timestamp(6) not null,
    project_api_key varchar(255) not null unique,
    primary key (api_key)
);
create table projects
(
    api_key      varchar(255) not null,
    name         varchar(255) not null,
    description  varchar(255),
    created_at   timestamp(6) not null,
    prompt_text  varchar(255) not null,
    user_api_key varchar(255) not null,
    primary key (api_key)
);
create table project_fields
(
    name            varchar(255),
    type            char(1) not null check (type in ('S','I','F','B','D')),
    is_required     boolean,
    project_api_key varchar(255) not null
);
create table users
(
    api_key           varchar(255) not null,
    username          varchar(255) not null unique,
    email             varchar(255) not null unique,
    password          varchar(255) not null,
    role              char(1) not null check (role in ('U','A')),
    created_at        timestamp(6) not null,
    last_login        timestamp(6),
    is_active         boolean      not null,
    is_email_verified boolean      not null,
    session_token     varchar(255),
    primary key (api_key)
);
alter table if exists api_access_tokens add constraint FKfmk5r3j4kq5q1if7k7irm851b foreign key (project_api_key) references projects;
alter table if exists projects add constraint FKdrfy28e05qstetv88oq1s9vnu foreign key (user_api_key) references "users";
alter table if exists project_fields add constraint FKp0d4cfs9ng3id780xbhqjddxv foreign key (project_api_key) references projects;
