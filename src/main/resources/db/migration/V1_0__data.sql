create table if not exists user
(
  id       int auto_increment
    primary key,
  email    varchar(50)  not null,
  login    varchar(50)  not null,
  password varchar(255) not null,
  constraint UK_login
    unique (login),
  constraint UK_email
    unique (email)
);

create table user_roles
(
	user_id int not null,
	roles varchar(255) null
);
create index FK55itppkw3i07do3h7qoclqd4k
	on user_roles (user_id);





