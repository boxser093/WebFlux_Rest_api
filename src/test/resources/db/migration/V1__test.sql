CREATE TABLE test_bd_for_rest.files
(
    id       int(99) primary key auto_increment,
    fileName text(64)  not null,
    filePath text(256) not null,
    status   text      not null
);

CREATE TABLE test_bd_for_rest.users
(
    id         int primary key auto_increment,
    username   varchar(64)   not null unique,
    password   varchar(2048) not null,
    role       varchar(32)   not null,
    first_name varchar(64)   not null,
    last_name  varchar(64)   not null,
    status     text not null,
    created_At timestamp not null,
    updated_At timestamp not null,
    eventId    int references events (userId) ON UPDATE CASCADE
);

CREATE TABLE test_bd_for_rest.events
(
    id     int primary key auto_increment,
    userId int  not null references users (id) ON UPDATE CASCADE,
    fileId int  not null references files (id) ON UPDATE CASCADE,
    status text not null
);

insert into test_bd_for_rest.users(username, password, role, first_name, last_name, status, created_at, updated_at, eventid)
values ('admin', 'N1/RcZnu10TiUEhNbk4wMsPjiz1CO8kuSi39r/i+Gkw=', 'ADMIN','John', 'Cena', 'ACTIVE', '2024-03-04 14:30:44', '2024-03-04 14:30:44', null);
