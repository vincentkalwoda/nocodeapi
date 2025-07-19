create table prompt_history
(
    value           varchar(255) not null,
    content         TEXT         not null,
    created_at      timestamp(6) not null,
    role            varchar(255) not null check (role in ('USER', 'SYSTEM')),
    project_api_key varchar(255) not null,
    primary key (value)
);

alter table if exists prompt_history
    add constraint FKg0sgyc0a2pwwis74m5s7cyq49
    foreign key (project_api_key)
    references projects
    on
delete
cascade;

alter table projects drop column if exists prompt_text;