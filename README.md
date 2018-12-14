# 2018-eCommerce
Assignment for the 2018 edition of the "Web Development and the Semantic Web" course, by Douglas Funayama Tavares and Vitor Henrique de Moraes Escalfoni.

Technology involved: Spring Boot, Spring MVC, Spring Data, Spring Security, Tomcat, MySQL, Maven, Jena, Java and Docker. 

## Installation Guide
It will be presented a variety of ways to deploy and test the application. For all of those you need to clone this repository to your machine, and follow the respective guide to deploy.

```
git clone https://github.com/dwws-ufes/2018-eCommerce.git
cd 2018-eCommerce
```

### Deploy via IDE (Eclipse)

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

7. Run it as a Sping Boot app (mvn spring-boot:run)

### Deploy via Maven
Make sure you have the MySQL server running and the schema setup as in the previous section, otherwise the project won't build.

1. mvn clean
2. mvn clean install
3. Go to the target folder
4. java -jar ecommerce-0.0.1-SNAPSHOT.jar

## Docker
Make sure you have Docker Community Edition installed ([Installation Guide](https://docs.docker.com/install/)) and Docker-compose ([Installation Guide](https://docs.docker.com/compose/install/)).

There's also need to have the project build with Maven. As our MySQL database will be inside a docker container also, we're going to build the application skipping the tests:

```
mvn clean install -DskipTests
```

### Deploy via Docker CLI
Initialize MySQL (make sure it is not running already in your computer):
```
docker run -p 3306:3306 --name=docker-mysql --env="MYSQL_ROOT_PASSWORD=Root123" --env="MYSQL_PASSWORD=Root123" --env="MYSQL_DATABASE=sys" mysql
```

Build the project:
```
docker build -t ecommerce-docker .
```

Run it:
```
docker run -t --name ecommerce --link docker-mysql:mysql -p 8080:8080 ecommerce-docker .
```

### Deploy via Docker-compose
Simply run 
```
docker-compose up
```
(Takes a while to start the because it waits for the db container to be healthy, but gitve it a chance)

## Testing the application
Make sure you didn't had compilation or communication problems and open http://localhost:8080/ on your favorite browser!