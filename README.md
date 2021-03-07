Task API
-----------------------
#### Assignment description for the Java Developer position:
Create an API application in Java.

#### Bonus assignment: 
Create RestAPI in which would be possible to maintain tasks with following functions:

    * Add a task
    * Change a task
    * Delete a task
    * Task completed

## Solution

### Technologies

- Java 14
- Spring Framework 5
- Spring Data JPA
- Spring Web MVC
- Spring Test
- HSQLDB
- PostgreSQL
- Hibernate ORM
- Apache Tomcat
- Jackson JSON
- JUnit 5
- Hamcrest
- AssertJ
- JSONassert
- Logback
- SLF4J

### How To Run

* Build and package with Apache Maven or use already prepared task-api.war from the root directory of the project.  
  By-default application runs using embedded HSQLDB.  
  If you want to run it with Postgres then you need to change "spring.profiles.default" parameter in web.xml from "hsqldb" to "postgres"
* Deploy to Apache Tomcat (app tested with 9.0.41 version)