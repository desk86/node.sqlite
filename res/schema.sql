CREATE TABLE `node` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`admin`	INTEGER,
	`content`	TEXT,
	`editors`	TEXT
);

CREATE TABLE `synapse` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`editor`	INTEGER,
	`parent`	INTEGER,
	`content`	TEXT,
	`timestamp`	INTEGER
);

CREATE TABLE `editor` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`name`	TEXT,
	`email`	TEXT,
	`pass`	TEXT,
	`invitations`	TEXT,
	`node`	INTEGER
);