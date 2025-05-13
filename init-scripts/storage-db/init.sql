create table storage
(
    id   bigserial primary key,
    storage_type       varchar(255),
    bucket       varchar(255),
    path       varchar(255)
);
