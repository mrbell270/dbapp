create table users (
  id int not null primary key auto_increment,
  username varchar(100) unique not null,
  passwordhash varchar(100) not null,
  description varchar(100) 
);

create table sessions (
  id int primary key not null,
  foreign key(id) references users(id),
  token varchar(100) not null unique,
  expiration_date timestamp not null
);

create table orders (
  id int primary key not null auto_increment,
  userid int not null,
  foreign key(userid) references users(id)
);

create table billings (
  id int primary key not null,
  foreign key(id) references orders(id),
  cost int not null
);

create table goods (
  id int not null primary key auto_increment,
  name varchar(100) not null,
  description varchar(100),
  cost int not null
);

create table goods2orders (
  id int not null primary key auto_increment,
  orderid int,
  foreign key(orderid) references orders(id),
  goodid int,
  foreign key(goodid) references goods(id)
);
