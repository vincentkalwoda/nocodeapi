DROP TABLE IF EXISTS project_fields;

CREATE TABLE entities (
    api_key varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    project_api_key varchar(255) NOT NULL,
    PRIMARY KEY (api_key)
);

CREATE TABLE fields (
    api_key varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    type varchar(10) NOT NULL CHECK (type IN ('STRING','INTEGER','BOOLEAN','FLOAT','DATE')),
    is_required boolean NOT NULL,
    relation_type smallint CHECK (relation_type BETWEEN 0 AND 3),
    entity_api_key varchar(255) NOT NULL,
    relation_target_api_key varchar(255),
    PRIMARY KEY (api_key)
);

ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('U','A'));

ALTER TABLE entities
    ADD CONSTRAINT FKr9guo6enh830auyxopt973dt8
    FOREIGN KEY (project_api_key)
    REFERENCES projects(api_key);

ALTER TABLE fields
    ADD CONSTRAINT FK6h00xton7up1llv45cccwjlc
    FOREIGN KEY (entity_api_key)
    REFERENCES entities(api_key);

ALTER TABLE api_access_tokens DROP CONSTRAINT IF EXISTS FKfmk5r3j4kq5q1if7k7irm851b;
ALTER TABLE api_access_tokens
    ADD CONSTRAINT FKpf46hglbmlw2x0lpnbq1ik1v8
    FOREIGN KEY (project_api_key)
    REFERENCES projects(api_key);

ALTER TABLE projects DROP CONSTRAINT IF EXISTS FKdrfy28e05qstetv88oq1s9vnu;
ALTER TABLE projects
    ADD CONSTRAINT FKjt7885km1ynf94n73ve21y6xx
    FOREIGN KEY (user_api_key)
    REFERENCES users(api_key);