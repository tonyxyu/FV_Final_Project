DROP DATABASE IF EXISTS organization_management;

CREATE DATABASE organization_management;
USE organization_management;

DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;
DROP TABLE IF EXISTS organizations;

CREATE TABLE organizations (
    organization_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    details JSON
);

CREATE TABLE departments (
    department_id INT PRIMARY KEY,
    organization_id INT,
    name VARCHAR(255) NOT NULL,
    head_employee_id INT,
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id)
);

CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    organization_id INT,
    department_id INT,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(100),
    hire_date DATE,
    salary DECIMAL(10, 2),
    performance DECIMAL(5, 2),
    contact_info JSON,
    FOREIGN KEY (organization_id) REFERENCES organizations(organization_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

CREATE TABLE shifts (
    organization_id INT NOT NULL,
    employee_id INT NOT NULL,
    day_of_week INT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7), -- DayOfWeek enum uses 1-7
    time_slot INT NOT NULL CHECK (time_slot BETWEEN 0 AND 2),
    PRIMARY KEY (organization_id, employee_id, day_of_week, time_slot),
    FOREIGN KEY (organization_id, employee_id) REFERENCES employees(organization_id, employee_id)
);

-- Add foreign key constraint for department head after employees table is created
ALTER TABLE departments
ADD CONSTRAINT fk_department_head
FOREIGN KEY (head_employee_id) REFERENCES employees(employee_id);

INSERT INTO organizations (organization_id, name, details) VALUES
(1, 'Acme Corp', '{"founded": "1990-01-01", "industry": "Technology"}'),
(2, 'Beta Inc', '{"founded": "2000-05-15", "industry": "Finance"}');

-- Departments for clientId = 1
INSERT INTO departments (department_id, organization_id, name) VALUES
(10001, 1, 'Engineering'),  -- internalId = 1*10000 + 1
(10002, 1, 'Marketing');    -- internalId = 1*10000 + 2

-- Departments for clientId = 2
INSERT INTO departments (department_id, organization_id, name) VALUES
(20001, 2, 'Research'),     -- internalId = 2*10000 + 1
(20002, 2, 'Sales');        -- internalId = 2*10000 + 2

-- Employees for clientId = 1
INSERT INTO employees (employee_id, organization_id, department_id, name, position, hire_date, salary, performance, contact_info) VALUES
(10001, 1, 10001, 'John Doe', 'Software Engineer', '2020-01-15', 75000.00, 90.00, '{"email": "john.doe@acme.com", "phone": "123-456-7890"}'),
(10002, 1, 10002, 'Jane Smith', 'Marketing Manager', '2019-05-01', 80000.00, 85.50, '{"email": "jane.smith@acme.com", "phone": "098-765-4321"}'),
(10003, 1, 10001, 'Tom Brown', 'Software Engineer', '2021-03-20', 70000.00, 88.25, '{"email": "tom.brown@acme.com", "phone": "123-456-7890"}');

-- Employees for clientId = 2
INSERT INTO employees (employee_id, organization_id, department_id, name, position, hire_date, salary, performance, contact_info) VALUES
(20001, 2, 20001, 'Alice Johnson', 'Research Scientist', '2018-03-10', 90000.00, 92.75, '{"email": "alice.johnson@beta.com", "phone": "555-555-5555"}'),
(20002, 2, 20002, 'Bob Brown', 'Sales Associate', '2021-07-22', 60000.00, 80.00, '{"email": "bob.brown@beta.com", "phone": "444-444-4444"}');

-- Insert shift assignments for Acme Corp (organization_id = 1)

-- John Doe's shifts (employee_id = 10001)
INSERT INTO shifts (organization_id, employee_id, day_of_week, time_slot) VALUES
                                                                              (1, 10001, 1, 0),  -- Monday morning (9-12)
                                                                              (1, 10001, 3, 1),  -- Wednesday afternoon (2-5)
                                                                              (1, 10001, 5, 2);  -- Friday evening (6-9)

-- Jane Smith's shifts (employee_id = 10002)
INSERT INTO shifts (organization_id, employee_id, day_of_week, time_slot) VALUES
                                                                              (1, 10002, 2, 0),  -- Tuesday morning (9-12)
                                                                              (1, 10002, 4, 1),  -- Thursday afternoon (2-5)
                                                                              (1, 10002, 5, 0);  -- Friday morning (9-12)

-- Tom Brown's shifts (employee_id = 10003)
INSERT INTO shifts (organization_id, employee_id, day_of_week, time_slot) VALUES
                                                                              (1, 10003, 1, 1),  -- Monday afternoon (2-5)
                                                                              (1, 10003, 3, 0),  -- Wednesday morning (9-12)
                                                                              (1, 10003, 5, 1);  -- Friday afternoon (2-5)

-- Insert shift assignments for Beta Inc (organization_id = 2)

-- Alice Johnson's shifts (employee_id = 20001)
INSERT INTO shifts (organization_id, employee_id, day_of_week, time_slot) VALUES
                                                                              (2, 20001, 1, 0),  -- Monday morning (9-12)
                                                                              (2, 20001, 2, 1),  -- Tuesday afternoon (2-5)
                                                                              (2, 20001, 4, 2);  -- Thursday evening (6-9)

-- Bob Brown's shifts (employee_id = 20002)
INSERT INTO shifts (organization_id, employee_id, day_of_week, time_slot) VALUES
                                                                              (2, 20002, 2, 0),  -- Tuesday morning (9-12)
                                                                              (2, 20002, 3, 1),  -- Wednesday afternoon (2-5)
                                                                              (2, 20002, 5, 2);  -- Friday evening (6-9)

-- Update department heads
UPDATE departments SET head_employee_id = 10001 WHERE department_id = 10001;
UPDATE departments SET head_employee_id = 10002 WHERE department_id = 10002;
UPDATE departments SET head_employee_id = 20001 WHERE department_id = 20001;
UPDATE departments SET head_employee_id = 20002 WHERE department_id = 20002;
