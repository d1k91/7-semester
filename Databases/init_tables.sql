create table prod(
pnum int primary key,
name varchar(20) not null,
weight int not null,
city varchar(20) not null);

create table cust(
cnum int primary key,
name varchar(20) not null,
rating int not null,
city varchar(20) not null);

create table sal(
snum int primary key,
name varchar(20) not null,
comm numeric(7,2) not null,
city varchar(20) not null);

create table ord(
onum int primary key,
pnum int not null,
cnum int not null,
snum int not null,
amt int not null,
foreign key (pnum) references prod(pnum),
foreign key (cnum) references cust(cnum),
foreign key (snum) references sal(snum));

insert into prod values (1001, 'Monitor',2000,'Obninsk');
insert into prod values (1002, 'Keyboard',500,'Yekaterinburg' );
insert into prod values (1003, 'Mouse', 100,'Novosibirsk');
insert into prod values (1004, 'Printer',1500,'Saint Petersburg' );
insert into prod values (1005, 'Hard drive',300, 'Moscow');
insert into prod values (1006, 'Speakers', 700,'Novosibirsk');

insert into cust values (2001, 'Ivanov', 100, 'Perm');
insert into cust values (2002, 'Petrov', 100, 'Moscow');
insert into cust values (2003, 'Vasiliev', 200, 'Yekaterinburg');
insert into cust values (2004, 'Dmitriev', 200, 'Krasnoyarsk');
insert into cust values (2005, 'Skvortcov', 300, 'Moscow');
insert into cust values (2006, 'Avdeev', 400, 'Novosibirsk');
insert into cust values (2007, 'Smirnov', 100, 'Omsk');

insert into sal values (3001, 'DNS', 0.11, 'Novosibirsk');
insert into sal values (3002, 'Citylink', 0.12, 'Saint Petersburg');
insert into sal values (3003, 'MVideo', 0.15, 'Yekaterinburg');
insert into sal values (3004, 'Inline', 0.13, 'Vladivistok');
insert into sal values (3005, 'Elbrus', 0.11, 'Moscow');

insert into ord values (4001, 1001, 2001, 3001, 2);
insert into ord values (4002, 1001, 2002, 3001, 1);
insert into ord values (4003, 1001, 2006, 3002, 10);
insert into ord values (4004, 1001, 2003, 3003, 5);
insert into ord values (4005, 1001, 2004, 3004, 5);
insert into ord values (4006, 1002, 2001, 3005, 7);
insert into ord values (4007, 1002, 2002, 3001, 8);
insert into ord values (4008, 1003, 2001, 3002, 3);
insert into ord values (4009, 1003, 2006, 3003, 1);
insert into ord values (4010, 1003, 2007, 3004, 9);
insert into ord values (4011, 1003, 2004, 3003, 6);
insert into ord values (4012, 1004, 2002, 3001, 6);
insert into ord values (4013, 1004, 2001, 3002, 4);
insert into ord values (4014, 1004, 2001, 3004, 4);
insert into ord values (4015, 1004, 2006, 3003, 3);
insert into ord values (4016, 1004, 2004, 3005, 3);
insert into ord values (4017, 1004, 2007, 3002, 2);
insert into ord values (4018, 1005, 2001, 3005, 1);
insert into ord values (4019, 1006, 2004, 3005, 2);
insert into ord values (4020, 1006, 2003, 3002, 3);