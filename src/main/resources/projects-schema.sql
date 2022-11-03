DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS step CASCADE;
DROP TABLE IF EXISTS material CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS project;



CREATE TABLE IF NOT EXISTS project (
	project_id int AUTO_INCREMENT NOT NULL,
	project_name varchar(128) NOT NULL,
	estimated_hours decimal(7,2),
	actual_hours decimal(7,2),
	difficulty int,
	notes TEXT,
	PRIMARY KEY(project_id)
);

CREATE TABLE IF NOT EXISTS category(
	category_id int AUTO_INCREMENT NOT NULL,
	category_name varchar(128) NOT NULL,
	PRIMARY KEY(category_id)
);

CREATE TABLE IF NOT EXISTS material(
	material_id int AUTO_INCREMENT NOT NULL,
	project_id int not NULL,
	material_name varchar(128) NOT NULL,
	num_required int,
	cost decimal(7,2),
	FOREIGN KEY(project_id) REFERENCES project(project_id) ON DELETE CASCADE,
	PRIMARY KEY(material_id)
);

CREATE TABLE IF NOT EXISTS step(
	step_id int NOT NULL AUTO_INCREMENT,
	project_id int NOT NULL,
	step_text TEXT NOT NULL,
	step_order int NOT NULL,
	PRIMARY KEY(step_id),
	FOREIGN KEY (project_id) REFERENCES project(project_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_category(
	project_id int not NULL,
	category_id int not NULL,
	FOREIGN KEY (project_id) references project(project_id) ON DELETE CASCADE,
	FOREIGN KEY (category_id) references category(category_id)
);