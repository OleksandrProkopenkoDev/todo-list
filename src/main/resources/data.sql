-- Insert users
INSERT INTO app_user (id, username, password)
VALUES (1, 'admin', '$2a$10$gcJhZecfudpQj6r1Zkvb1uR6jPNNJOU9aNRHxLnihPGKInBizYbHy');
INSERT INTO app_user (id, username, password)
VALUES (2, 'Alice', '$2a$10$whiSmKQdxD1nEIMgOzspfe5p8pelQ6kjMLSXedCwqm96Ov7rvh5UG');
INSERT INTO app_user (id, username, password)
VALUES (3, 'Bob', '$2a$10$fajuo8L5u0ztY3dzhn4Zb.4ql4lcUCAFOeyKVYhzSndDYGZNMy0Wa');

-- Insert tasks for Alice
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (1, 'Task 1 for Alice', 'Description for Task 1', '2024-09-20T10:00:00', false, 2),
       (2, 'Task 2 for Alice', 'Description for Task 2', '2024-09-22T15:00:00', true, 2),
       (3, 'Task 3 for Alice', 'Description for Task 3', '2024-09-25T12:00:00', false, 2);

-- Insert tasks for Bob
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (4, 'Task 1 for Bob', 'Description for Task 1', '2024-09-21T08:00:00', false, 3),
       (5, 'Task 2 for Bob', 'Description for Task 2', '2024-09-23T09:00:00', true, 3),
       (6, 'Task 3 for Bob', 'Description for Task 3', '2024-09-26T14:00:00', false, 3);

-- Continue inserting more tasks for both Alice and Bob (up to 30+ tasks)
-- You can randomize the dates and completion status

-- Insert file attachments for some tasks
INSERT INTO file_attachment (id, file_name, data, content_type, task_id)
VALUES (1, 'file1.png', null, 'image/png', 1),
       (2, 'file2.docx', null,
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 2),
       (3, 'file3.pdf', null, 'application/pdf', 4);

-- Some tasks can have no attachments
-- Insert tasks for Admin
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (7, 'Admin - Project Planning', 'Complete the project roadmap for Q4', '2024-09-18T09:00:00',
        false, 1);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (8, 'Admin - Budget Review', 'Review the budget for upcoming releases',
        '2024-09-19T14:30:00', true, 1);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (9, 'Admin - Team Meeting', 'Organize a weekly meeting with the development team',
        '2024-09-20T10:00:00', false, 1);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (10, 'Admin - KPI Evaluation', 'Evaluate KPIs for all team members', '2024-09-25T13:00:00',
        true, 1);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (11, 'Admin - Security Audit', 'Perform a security audit on the current infrastructure',
        '2024-09-28T11:00:00', false, 1);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (12, 'Admin - Quarterly Review', 'Prepare for the quarterly review with stakeholders',
        '2024-09-30T16:00:00', false, 1);

-- Insert tasks for Alice
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (13, 'Alice - Update Design Mockups', 'Update mockups for the new homepage design',
        '2024-09-19T11:00:00', true, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (14, 'Alice - User Feedback Analysis', 'Analyze the latest user feedback from the survey',
        '2024-09-21T09:00:00', false, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (15, 'Alice - Fix CSS Issues', 'Resolve cross-browser compatibility issues',
        '2024-09-23T17:00:00', false, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (16, 'Alice - Performance Improvements',
        'Optimize website performance by reducing image sizes', '2024-09-25T14:00:00', true, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (17, 'Alice - Newsletter Design', 'Design the layout for the September newsletter',
        '2024-09-27T08:00:00', false, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (18, 'Alice - Content Updates', 'Update the About Us page with the latest company history',
        '2024-09-29T12:00:00', false, 2);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (19, 'Alice - Image Gallery Enhancement', 'Enhance the image gallery with filtering options',
        '2024-10-01T10:00:00', true, 2);

-- Insert tasks for Bob
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (20, 'Bob - API Integration', 'Integrate third-party API for payment processing',
        '2024-09-18T14:00:00', false, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (21, 'Bob - Bug Fixing Sprint', 'Fix critical bugs reported in the latest release',
        '2024-09-20T09:00:00', true, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (22, 'Bob - Database Optimization', 'Improve query performance for customer data retrieval',
        '2024-09-22T15:30:00', false, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (23, 'Bob - Unit Testing', 'Write unit tests for new API endpoints', '2024-09-24T10:00:00',
        false, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (24, 'Bob - Documentation Update', 'Update technical documentation for the API',
        '2024-09-26T13:00:00', true, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (25, 'Bob - Monitoring Setup', 'Set up monitoring for the production environment',
        '2024-09-28T12:00:00', false, 3);
INSERT INTO task (id, title, description, due_date, completed, user_id)
VALUES (26, 'Bob - Database Backup Plan', 'Develop a backup and recovery plan for databases',
        '2024-09-30T11:00:00', true, 3);

-- File attachments (no change)
INSERT INTO file_attachment (id, file_name, data, content_type, task_id)
VALUES (4, 'project_roadmap.xlsx', null,
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 7),
       (5, 'budget_review.pdf', null, 'application/pdf', 8),
       (6, 'kpi_report.docx', null,
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 10),
       (7, 'security_audit_report.docx', null,
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 11);

INSERT INTO file_attachment (id, file_name, data, content_type, task_id)
VALUES (8, 'mockup_update.png', null, 'image/png', 13),
       (9, 'feedback_analysis.xlsx', null,
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 14),
       (10, 'performance_report.pdf', null, 'application/pdf', 16);

INSERT INTO file_attachment (id, file_name, data, content_type, task_id)
VALUES (11, 'api_integration_guide.pdf', null, 'application/pdf', 20),
       (12, 'bug_fix_list.docx', null,
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 21),
       (13, 'unit_tests.zip', null, 'application/zip', 23),
       (14, 'monitoring_setup.pptx', null,
        'application/vnd.openxmlformats-officedocument.presentationml.presentation', 25);

-- Reset sequence for `task` table
CREATE TEMPORARY TABLE temp_max_id_task AS
SELECT MAX(id) AS max_id
FROM task;
SET @next_id_task = (SELECT max_id + 1
                     FROM temp_max_id_task);
ALTER TABLE task
    ALTER COLUMN id RESTART WITH @next_id_task;
DROP TABLE temp_max_id_task;

-- Reset sequence for `app_user` table
CREATE TEMPORARY TABLE temp_max_id_user AS
SELECT MAX(id) AS max_id
FROM app_user;
SET @next_id_user = (SELECT max_id + 1
                     FROM temp_max_id_user);
ALTER TABLE app_user
    ALTER COLUMN id RESTART WITH @next_id_user;
DROP TABLE temp_max_id_user;

-- Reset sequence for `file_attachment` table
CREATE TEMPORARY TABLE temp_max_id_attachment AS
SELECT MAX(id) AS max_id
FROM file_attachment;
SET @next_id_attachment = (SELECT max_id + 1
                           FROM temp_max_id_attachment);
ALTER TABLE file_attachment
    ALTER COLUMN id RESTART WITH @next_id_attachment;
DROP TABLE temp_max_id_attachment;
