
create table account(
                        id int primary key not null auto_increment,
                        user_name varchar(64) not null unique,
                        hashed_password varchar(128) not null,
                        salt varchar(32) not null,
                        created_time datetime default now()
)engine = InnoDB, charset = utf8mb4;


create table player (
                        account_id integer,
                        arm_life integer not null,
                        head_life integer not null,
                        inner_power integer not null,
                        inner_power_exp integer not null,
                        leg_life integer not null,
                        life integer not null,
                        male bit not null,
                        outer_power integer not null,
                        outer_power_exp integer not null,
                        power integer not null,
                        power_exp integer not null,
                        realm_id integer not null,
                        revival_exp integer not null,
                        state integer not null,
                        x integer not null,
                        y integer not null,
                        yang integer not null,
                        yin integer not null,
                        id bigint not null,
                        `name` varchar(32) not null,
                        primary key (id)) engine=InnoDB, charset = utf8mb4;


create table player_kung_fu (
                         exp integer not null,
                         slot integer not null,
                         player_id bigint not null,
                         `name` varchar(32) not null,
                         primary key (player_id, `name`)) engine=InnoDB, charset=utf8mb4;

CREATE TABLE `player_seq` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;
insert into player_seq(next_val) values(100000000);

CREATE TABLE `player_equipment` (
                             player_id bigint not null,
                             `name` varchar(32) not null,
                             color int not null default 0,
                             primary key (player_id, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;

create table player_item (
                      `number` bigint not null,
                      color int not null default 0,
                      slot integer not null,
                      player_id bigint not null,
                      `name` varchar(32) not null,
                      `type` varchar(16) not null comment 'BANK or INVENTORY',
                      primary key (player_id, `slot`, `type`)) engine=InnoDB, charset=utf8mb4;


create table bank (
	id bigint primary key auto_increment,
	player_id bigint not null unique,
	capacity integer not null,
    unlocked integer not null
) ENGINE=InnoDB;


create table attack_kungfu(
id integer not null primary key auto_increment,
`name` varchar(12) not null unique,
`type` varchar(12) not null,
attack_speed integer not null,
recovery integer not null,
avoid integer not null,
body_damage integer not null,
head_damage integer not null,
arm_damage integer not null,
leg_damage integer not null,
body_armor integer not null,
head_armor integer not null,
arm_armor integer not null,
leg_armor integer not null,
swing_life integer not null,
swing_inner_power integer not null,
swing_power integer not null,
swing_outer_power integer not null,
strike_sound integer not null,
swing_sound integer not null,
effect_color integer not null
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;