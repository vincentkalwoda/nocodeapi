create table field_constraints
(
    field_api_key   varchar(255)                                                   not null,
    constraint_type varchar(255) check (constraint_type in
                                        ('NOT_NULL', 'UNIQUE', 'MIN_LENGTH', 'MAX_LENGTH', 'REGEX', 'MIN', 'MAX',
                                         'DEFAULT', 'FOREIGN_KEY', 'PRIMARY_KEY')) not null,
    value           varchar(255)
);

alter table if exists field_constraints
    add constraint FKk4f8qtb4eti8wlf56f75avtgj
        foreign key (field_api_key)
            references fields
            on delete cascade;

alter table if exists field_constraints
    add column target_entity varchar(255);

alter table if exists field_constraints
    add column target_field varchar(255);

alter table if exists field_constraints
    add column relation_type varchar(255) check (relation_type in
                                                 ('ONE_TO_ONE', 'ONE_TO_MANY', 'MANY_TO_ONE', 'MANY_TO_MANY'));