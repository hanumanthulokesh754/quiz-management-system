# Online Quiz System (Java Servlets + JSP + JDBC + MySQL)

College Full-Stack Development project. Java 25, Tomcat 10.1 (`jakarta.servlet`), MySQL 8, Maven.
Students register, take timed quizzes and see their scores. Admins manage quizzes, questions and results.

## 1. Project structure (MVC)

```
online-quiz-system/
├── pom.xml
├── sql/schema.sql                      database + tables + sample data
└── src/main/
    ├── java/com/quiz/
    │   ├── model/    User, Quiz, Question, Result          (M)
    │   ├── dao/      UserDAO, QuizDAO, QuestionDAO, ResultDAO   (JDBC)
    │   ├── servlet/  Register, Login, Logout, Dashboard, Quiz, SubmitQuiz,
    │   │             Result, Admin, Question servlets        (C)
    │   ├── filter/   SecurityFilter  (login/role checks + CSRF token)
    │   └── util/     DBConnection, PasswordUtil, WebUtil, CreateAdmin
    ├── resources/db.properties         database credentials
    └── webapp/
        ├── index.jsp, login.jsp, register.jsp      public pages
        ├── css/style.css
        └── WEB-INF/
            ├── web.xml
            └── views/                              (V) protected pages, only reachable through servlets
                student-dashboard, quiz, result, quiz-history,
                admin-dashboard, add-question, view-questions, view-results,
                error .jsp  +  header.jspf / footer.jspf
```

Servlets and the filter use `@WebServlet` / `@WebFilter` annotations; `web.xml` only holds settings (welcome page, session, error pages).

## 2. Prerequisites

JDK 25+, Maven 3.9+, MySQL 8+, Apache Tomcat 10.1.x (NOT 9 or 11: 10.1 matches `jakarta.servlet` 6.0).
The pages load Bootstrap from a CDN, so the browser needs internet access.

## 3. Create the database

```
mysql -u root -p < sql/schema.sql
```
(or open `schema.sql` in MySQL Workbench and run it). This **drops and recreates** `online_quiz`.

## 4. Configure credentials

Edit `src/main/resources/db.properties` and set `db.username` and `db.password`
(change the host/port in `db.url` if MySQL is not on `localhost:3306`).

## 5. Build

```
mvn clean package
```
Output: `target/online-quiz.war`

## 6. Deploy and run on Tomcat

**Option A, manual:** copy `target/online-quiz.war` into `<TOMCAT_HOME>/webapps/`, then start Tomcat
(`bin/startup.sh` on Linux/macOS, `bin\startup.bat` on Windows).

**Option B, IDE:** IntelliJ IDEA Ultimate / Eclipse / VS Code (Community Server Connectors): add a Tomcat 10.1 server and deploy the WAR (or exploded artifact) with context path `/online-quiz`.

Open: **http://localhost:8080/online-quiz/**

## 7. Test accounts

| Role    | Email              | Password     |
|---------|--------------------|--------------|
| Admin   | admin@quiz.com     | Admin@123    |
| Student | student@quiz.com   | Student@123  |

These come from `schema.sql` and are **for testing only**. Students can also self-register.

### Creating a real admin account securely
Passwords are stored as BCrypt hashes, and the registration form can only create STUDENT accounts. To create an admin:
```
mvn compile exec:java -Dexec.mainClass=com.quiz.util.CreateAdmin
```
It asks for name, email and password and stores a hashed password. Afterwards delete the sample admin:
```
DELETE FROM users WHERE email = 'admin@quiz.com';
```

## 8. Feature walkthrough (handy for the project review)

Student: register, login, dashboard of quizzes, take quiz (radio buttons + countdown timer), submit, score page (correct / incorrect / total / %), "My Results" history, logout.
Admin: login, dashboard with counts, create quiz, add / view / edit / delete questions (filter by quiz), view every student's results.

Rules: each student can take each quiz **once** (unique key on `results(user_id, quiz_id)`). Deleting that result row in MySQL lets a student retake it.

## 9. How the security requirements are met

| Requirement | Where |
|---|---|
| Prepared statements everywhere | all `dao/*.java` |
| Sessions for login | `LoginServlet` (new session id after login), `LogoutServlet` invalidates |
| Server-side role enforcement | `SecurityFilter`: `/student/*` = STUDENT, `/admin/*` = ADMIN |
| Hashed passwords | `PasswordUtil` (BCrypt via jBCrypt) |
| Correct answers hidden | `QuestionDAO.findByQuizId(id, false)` never loads them for students |
| Score calculated on server | `SubmitQuizServlet` compares answers with the database |
| Protected pages not reachable directly | JSPs live in `WEB-INF/views`, only servlets forward to them |
| Validation + DB error handling | servlets validate; `SQLException` goes to `WebUtil.showError` (logged, friendly page) |
| POST for changes | register, login, logout, submit, add/edit/delete question, add quiz |
| No duplicate submissions | submit button disabled + server check + database unique key |
| Post/Redirect/Get | every successful POST ends with `sendRedirect` |
| Extra: CSRF token, XSS-safe output (`<c:out>`), no-cache on protected pages | `SecurityFilter`, JSPs |

## 10. Troubleshooting

**HTTP 404 on `/online-quiz/`**: WAR not deployed or wrong name. Check `webapps/online-quiz.war` unpacked into `webapps/online-quiz/` and read `logs/catalina.*.log`.

**`ClassNotFoundException` / `NoClassDefFoundError: javax/servlet/...`**: you are on Tomcat 9 or older. Use Tomcat 10.1.

**`UnsupportedClassVersionError`**: Tomcat is running on an older JDK than 17. Check `JAVA_HOME` / `java -version`.

**`No suitable driver` / `ClassNotFoundException: com.mysql.cj.jdbc.Driver`**: run `mvn clean package` again and check that `WEB-INF/lib/mysql-connector-j-*.jar` is inside the WAR.

**`Access denied for user 'root'@'localhost'`**: wrong `db.username` / `db.password` in `db.properties` (rebuild after editing).

**`Unknown database 'online_quiz'` / `Table ... doesn't exist`**: `schema.sql` was not run.

**`Communications link failure`**: MySQL is not running or host/port in `db.url` is wrong.

**`Public Key Retrieval is not allowed`**: keep `allowPublicKeyRetrieval=true` in `db.url` (already included).

**JSP shows `${...}` as text or `The absolute uri: jakarta.tags.core cannot be resolved`**: the JSTL jars are missing. Rebuild with Maven and make sure `jakarta.servlet.jsp.jstl-*.jar` is in `WEB-INF/lib`. The URIs must be `jakarta.tags.core`, not `http://java.sun.com/jsp/jstl/core`.

**Error 403 after submitting a form**: the page was open too long (session expired) or cookies are blocked. Reload the page and try again.

**Login works but you are sent back to login**: the browser blocks cookies, or the session timed out (30 min).

**Page has no styling**: Bootstrap comes from a CDN, so check your internet connection; `css/style.css` is local.

**Port 8080 already in use**: stop the other program or change the port in `<TOMCAT_HOME>/conf/server.xml`.

**`mvn` not found / compile errors on `release 25`**: install JDK 25+ and set `JAVA_HOME`.

**Changes not showing after redeploy**: delete `webapps/online-quiz/` and `work/Catalina/` folders, copy the new WAR and restart.
