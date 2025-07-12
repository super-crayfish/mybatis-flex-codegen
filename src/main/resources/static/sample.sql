CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL COMMENT 'User login name',
  password VARCHAR(100) NOT NULL COMMENT 'User password',
  email VARCHAR(100) COMMENT 'User email address',
  phone VARCHAR(20) COMMENT 'User phone number',
  status TINYINT DEFAULT 1 COMMENT 'User status: 0-disabled, 1-enabled',
  created_at DATETIME COMMENT 'Creation time',
  updated_at DATETIME COMMENT 'Last update time'
) COMMENT 'User information table';

CREATE TABLE role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT 'Role name',
  code VARCHAR(50) NOT NULL COMMENT 'Role code',
  description VARCHAR(200) COMMENT 'Role description',
  status TINYINT DEFAULT 1 COMMENT 'Role status: 0-disabled, 1-enabled',
  created_at DATETIME COMMENT 'Creation time',
  updated_at DATETIME COMMENT 'Last update time'
) COMMENT 'Role information table';

CREATE TABLE user_role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL COMMENT 'User ID',
  role_id INT NOT NULL COMMENT 'Role ID',
  created_at DATETIME COMMENT 'Creation time'
) COMMENT 'User-Role relationship table';