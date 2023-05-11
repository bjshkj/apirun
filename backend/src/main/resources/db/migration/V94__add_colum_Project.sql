ALTER TABLE project ADD git_path VARCHAR(200) COMMENT 'git url';
ALTER TABLE project ADD private_token VARCHAR(100) COMMENT 'git private-token';
ALTER TABLE project ADD project_type VARCHAR(100) COMMENT 'project HTTPRUNNER/JMETER';