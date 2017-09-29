CREATE TABLE `node` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`admin`	INTEGER,
	`content`	TEXT
);

CREATE TABLE `synapse` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`editor`	INTEGER,
	`parent`	INTEGER,
	`content`	TEXT,
	`timestamp`	INTEGER
);