# 2018-eCommerce
Assignment for the 2018 edition of the "Web Development and the Semantic Web" course, by Douglas Funayama Tavares and Vitor Henrique de Moraes Escalfoni.

## Install throught IDE (Eclipse)

1. Install [Eclipse IDE for Java EE Developers](https://www.eclipse.org/downloads/)

2. Install [Apache Tomcat 7.x](https://tomcat.apache.org/download-70.cgi), and configure it on Eclipse as a New Server and as the project's Target Runtime.

3. Install [MySQL](http://www.mysql.com/products/community/) and create a database schema called `sys` and a username `root` with `Root123` as password, with full granted permissions to the schema.

4. Configure `application.properties`, located on `src/main/resources`, with the Data Source information:

```XML
spring.datasource.url = jdbc:mysql://localhost:3306/sys?useSSL=false
spring.datasource.username = root
spring.datasource.password = Root123
```
5. Install [lombok](https://projectlombok.org/download) on Eclipse

6. Clone this repository and Import into Eclipse as an “Existing Maven Project”

## Install throught Maven

1. mvn clean
2. mvn clean install
3. Go to the target folder
4. java -jar login-0.0.1-SNAPSHOT.jar