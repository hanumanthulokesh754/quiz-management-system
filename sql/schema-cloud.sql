
CREATE TABLE users (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,               -- BCrypt hash (60 chars)
    role     ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT'
);

CREATE TABLE quizzes (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    quiz_name VARCHAR(150) NOT NULL,
    category  VARCHAR(100) NOT NULL,
    duration  INT NOT NULL,                       -- minutes
    CONSTRAINT chk_duration CHECK (duration > 0)
);

CREATE TABLE questions (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id        INT NOT NULL,
    question_text  TEXT NOT NULL,
    option_a       VARCHAR(255) NOT NULL,
    option_b       VARCHAR(255) NOT NULL,
    option_c       VARCHAR(255) NOT NULL,
    option_d       VARCHAR(255) NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    CONSTRAINT chk_correct CHECK (correct_answer IN ('A','B','C','D'))
);

CREATE TABLE results (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    quiz_id         INT NOT NULL,
    score           INT NOT NULL,                 -- number of correct answers
    total_questions INT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_results_user FOREIGN KEY (user_id) REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_results_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    -- one attempt per student per quiz (stops duplicate submissions even at database level)
    CONSTRAINT uq_user_quiz UNIQUE (user_id, quiz_id)
);

-- ---------------- Sample users (BCrypt hashes) ----------------
-- admin@quiz.com   / Admin@123    (FOR TESTING ONLY - delete or change before real use)
-- student@quiz.com / Student@123
INSERT INTO users (name, email, password, role) VALUES
('Quiz Admin',   'admin@quiz.com',   '$2a$10$x5d8Mvs7XRaLZwYgNvUJhOoDlk/aTpY7Z46USadOehwIro8UctyDu', 'ADMIN'),
('Sample Student','student@quiz.com','$2a$10$SZlbVrpFaXoXOrUKg30oyuZSqHeSRSc90IFEgXUUp2qLP.38qTA7i', 'STUDENT');

-- ---------------- Sample quizzes ----------------
INSERT INTO quizzes (quiz_name, category, duration) VALUES
('Java Basics',            'Programming', 10),
('SQL Fundamentals',       'Database',    10),
('Web Development Basics', 'Web',          8);

-- ---------------- Quiz 1: Java Basics ----------------
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
(1, 'Which keyword is used to inherit a class in Java?', 'implements', 'extends', 'inherits', 'super', 'B'),
(1, 'Which of these is NOT a primitive data type in Java?', 'int', 'boolean', 'String', 'char', 'C'),
(1, 'What is the default value of an int instance variable?', '0', 'null', '1', 'undefined', 'A'),
(1, 'Which method is the entry point of a Java application?', 'start()', 'run()', 'main()', 'init()', 'C'),
(1, 'Which collection does NOT allow duplicate elements?', 'ArrayList', 'HashSet', 'LinkedList', 'Vector', 'B');

-- ---------------- Quiz 2: SQL Fundamentals ----------------
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
(2, 'Which SQL statement is used to retrieve data from a table?', 'GET', 'OPEN', 'SELECT', 'EXTRACT', 'C'),
(2, 'Which clause is used to filter the rows returned by a query?', 'WHERE', 'ORDER BY', 'LIMIT', 'JOIN', 'A'),
(2, 'Which constraint uniquely identifies each row in a table?', 'FOREIGN KEY', 'PRIMARY KEY', 'CHECK', 'DEFAULT', 'B'),
(2, 'Which clause sorts the result set?', 'SORT BY', 'ORDER BY', 'GROUP BY', 'ARRANGE', 'B'),
(2, 'Which function returns the number of rows?', 'SUM()', 'COUNT()', 'TOTAL()', 'NUMBER()', 'B');

-- ---------------- Quiz 3: Web Development Basics ----------------
INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
(3, 'What does HTML stand for?', 'Hyper Text Markup Language', 'High Text Machine Language', 'Hyperlinks and Text Markup Language', 'Home Tool Markup Language', 'A'),
(3, 'Which CSS property changes the color of text?', 'font-color', 'text-color', 'color', 'foreground', 'C'),
(3, 'Which HTTP method is normally used to send form data that creates or changes data on the server?', 'GET', 'POST', 'HEAD', 'TRACE', 'B'),
(3, 'Which HTML tag creates a hyperlink?', '<link>', '<a>', '<href>', '<url>', 'B'),
(3, 'What is a JSP page compiled into by the server?', 'An applet', 'A servlet', 'Static HTML', 'A JavaBean', 'B');
