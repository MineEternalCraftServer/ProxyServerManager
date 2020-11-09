create table player_data
(
    id int auto_increment,
    mcid varchar(16) null,
    uuid varchar(36) null,
    discord_link varchar(32) null,
    isBanned boolean default false,
    isMuted boolean default false,
    ban_reason text null,
    mute_reason text null,
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

create table punish_log
(
    id int auto_increment,
    executioner_mcid varchar(16) null,
    executioner_uuid varchar(36) null,
    target_mcid varchar(16) null,
    target_uuid varchar(36) null,
    punish_type varchar(128) null,
    punish_reason text null,
    punish_date datetime null,
    constraint punish_log
        primary key (id)
);