DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS temp_user;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS download_type;
DROP TABLE IF EXISTS temp_download;
DROP TABLE IF EXISTS download;

CREATE TABLE role (
	id INT AUTO_INCREMENT PRIMARY KEY,
	role VARCHAR(8) NOT NULL
);

CREATE TABLE temp_user(
	email VARCHAR(100) PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	dob DATE NOT NULL,
	username VARCHAR(15) NOT NULL,
	password VARCHAR(255) NOT NULL,
	nic VARCHAR(15) NOT NULL
);

CREATE TABLE confirmation_token(
	id INT AUTO_INCREMENT PRIMARY KEY,
	token VARCHAR(255) NOT NULL,
	created_date TIMESTAMP NOT NULL,
	user_id VARCHAR(100) NOT NULL,
	FOREIGN KEY (user_id) REFERENCES temp_user (email) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE users(
	email VARCHAR(100) PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	dob DATE NOT NULL,
	username VARCHAR(15) NOT NULL,
	password VARCHAR(255) NOT NULL,
	nic VARCHAR(15) NOT NULL,
	role_id INT NOT NULL,
	FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE download_type(
	id INT AUTO_INCREMENT PRIMARY KEY,
	file_type VARCHAR(12) NOT NULL,
	default_path VARCHAR(255) NOT NULL
);

CREATE TABLE temp_download(
	id INT AUTO_INCREMENT PRIMARY KEY,
	url LONGTEXT NOT NULL,
	added_date DATE NOT NULL,
	last_modified VARCHAR(100) NOT NULL,
	name VARCHAR(100) NOT NULL,
	file_size DECIMAL(18,2) NOT NULL,
	added_by VARCHAR(100) NOT NULL,
	file_type_id INT NOT NULL,
	FOREIGN KEY (added_by) REFERENCES users (email) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (file_type_id) REFERENCES download_type (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE download(
	id INT AUTO_INCREMENT PRIMARY KEY,
	url LONGTEXT NOT NULL,
	added_date DATE NOT NULL,
	downloaded_date DATE NOT NULL,
	name VARCHAR(100) NOT NULL,
	file_size DECIMAL(18,2) NOT NULL,
	used_times INT NOT NULL,
	added_by VARCHAR(100) NOT NULL,
	file_type_id INT NOT NULL,
	FOREIGN KEY (added_by) REFERENCES users (email) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (file_type_id) REFERENCES download_type (id) ON DELETE CASCADE ON UPDATE CASCADE
);


INSERT INTO role(role) VALUES("ADMIN");
INSERT INTO role(role) VALUES("USER");

INSERT INTO users VALUES(
	"admin@nightwolf.com",
	"admin",
	"1980-12-20",
	"admin",
	"$2y$12$K.ps7FXPQpI0P7Q/WhV4VekEIqWfmTjBlsBsJi.kb/eQ2yYl9xl0S",
	"123456789V",
	"1"
);

INSERT INTO users VALUES(
	"user@nightwolf.com",
	"user",
	"1980-12-21",
	"user",
	"$2y$12$9lWxDMLsbyh24AWBV3M9iunlUcBsLrxdgwFxu4KlmOpl.K/N0QNPy",
	"012345678V",
	"2"
);


INSERT INTO download_type (file_type, default_path) VALUES (
  "documents",
  "C:\\Users\\abc\\Desktop\\Downloads\\Documents"
),
(
  "images",
  "C:\\Users\\abc\\Desktop\\Downloads\\Pictures"
),
(
  "audios",
  "C:\\Users\\abc\\Desktop\\Downloads\\Audios"
),
(
  "videos",
  "C:\\Users\\abc\\Desktop\\Downloads\\Videos"
),
(
  "programs",
  "C:\\Users\\abc\\Desktop\\Downloads\\Programs"
),
(
  "other",
  "C:\\Users\\abc\\Desktop\\Downloads\\Other"
);