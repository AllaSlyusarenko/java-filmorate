create table IF NOT EXISTS films(
id_film serial not null primary key,
name_film varchar(255) not null,
description varchar(255) not null,
release_date date not null,
duration int not null,
id_mpa int not NULL references mpa(id_mpa),
CONSTRAINT films_pk PRIMARY KEY (id_film)
);

create table IF NOT EXISTS users(
id_user serial not null primary key,
email varchar(255) not null,
login varchar(255) not null,
name_user varchar(255) not null,
birthday date not NULL,
CONSTRAINT users_pk PRIMARY KEY (id_user)
);

create table IF NOT EXISTS genres(
id_genre serial not null primary key,
name_genre varchar(255) not NULL,
CONSTRAINT genres_pk PRIMARY KEY (id_genre)
);

create table IF NOT EXISTS mpa(
id_mpa serial not null primary key,
name_mpa varchar(255) not NULL,
CONSTRAINT mpa_pk PRIMARY KEY (id_mpa)
);

create table IF NOT EXISTS filmGenre(
id_film int not null references films(id_film),
id_genre int not null references genres(id_genre),
PRIMARY KEY(id_film,id_genre)
);
create table IF NOT EXISTS likeUsers(
id_film int not null references films(id_film),
id_user int not null references users(id_user),
PRIMARY key(id_film,id_user)
);
create table IF NOT EXISTS friendship(
id_user int not null references users(id_user),
id_friend int not null references users(id_user),
status varchar(255) not NULL,
PRIMARY key(id_user,id_friend)
);