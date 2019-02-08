create table user
(
  id         int auto_increment
    primary key,
  login    varchar(255) NOT NULL,
  email      varchar(255) NOT NULL,
  password varchar(255) NOT NULL,

  constraint UK_email
  unique (email)
)
  engine = InnoDB;

  create table roles
(
  user_id int          not null,
  roles   varchar(255) null,
  constraint FK55itppkw3i07do3h7qoclqd4k
  foreign key (user_id) references user (id)
);

INSERT INTO user (id, login, password, email)
VALUES (default, 'test', '$2a$10$Yzz.g09WkIeQk1u8pdkehuO9nx1XxVxQj9Rds5j8ifTFrnNfZfGNe', 'inter32@list.ru');

INSERT INTO roles (user_id, roles) VALUES (1, 'ROLE_ADMIN');
