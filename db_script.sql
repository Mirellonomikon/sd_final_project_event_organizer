create table users
(
    id        serial
        primary key,
    username  varchar(255) not null
        unique,
    name      varchar(255) not null
        unique,
    password  varchar(255) not null,
    user_type varchar(50)  not null
        constraint users_user_type_check
            check ((user_type)::text = ANY
                   ((ARRAY ['client'::character varying, 'organizer'::character varying, 'administrator'::character varying])::text[])),
    email     varchar(255) not null
);

alter table users
    owner to postgres;

create table locations
(
    id       serial
        primary key,
    name     varchar(255) not null
        unique,
    address  text         not null,
    capacity integer      not null
);

alter table locations
    owner to postgres;

create table events
(
    id                serial
        primary key,
    name              varchar(255)      not null,
    event_type        varchar(100)      not null,
    event_date        date              not null,
    event_time        time,
    location_id       integer           not null
        references locations
            on delete set null,
    tickets_available integer           not null,
    price             numeric(10, 2)    not null,
    organizer_id      integer           not null
        references users
            on delete cascade,
    on_sale           integer default 0 not null
        constraint chk_on_sale
            check ((on_sale >= 0) AND (on_sale <= 100))
);

alter table events
    owner to postgres;

create table tickets
(
    id             serial
        primary key,
    user_id        integer        not null
        references users
            on delete cascade,
    event_id       integer        not null
        references events
            on delete cascade,
    purchase_price numeric(10, 2) not null
);

alter table tickets
    owner to postgres;

create table wishlist
(
    user_id  integer not null
        references users
            on delete cascade,
    event_id integer not null
        references events
            on delete cascade,
    primary key (user_id, event_id)
);

alter table wishlist
    owner to postgres;

