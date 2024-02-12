CREATE TABLE files
(
    id       int(99) primary key auto_increment not null,
    fileName text (64),
    filePath text (256),
    status   text
);

CREATE TABLE users
(
    id         int primary key auto_increment not null,
    username   varchar(64)                    not null unique,
    password   varchar(2048)                  not null,
    role       varchar(32)                    not null,
    first_name varchar(64)                    not null,
    last_name  varchar(64)                    not null,
    status     text,
    created_At timestamp,
    updated_At timestamp,
    event_id   int references events(user_id)
);

CREATE TABLE events
(
    id      int primary key auto_increment not null,
    user_id int references users (id) ON UPDATE CASCADE,
    file_id int references files (id) ON UPDATE CASCADE
);