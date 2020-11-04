create table player_data
(
    id int auto_increment,
    mcid varchar(16) null,
    uuid varchar(36) null,
    discord_link varchar(32) null,
    constraint player_data_pk
        primary key (id)
);

create table login_log
(
    id int auto_increment,
    mcid varchar(16) null,
    uuid varchar(36) null,
    address varchar(32) null,
    date datetime null,
    constraint login_log_pk
        primary key (id)
);

create table logout_log
(
    id int auto_increment,
    mcid varchar(16) null,
    uuid varchar(36) null,
    address varchar(32) null,
    date datetime null,
    constraint login_log_pk
        primary key (id)
);