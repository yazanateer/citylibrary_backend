**citylibrary Project - library management system** <br> <br><br>
This project is a RESTful API built with Java Spring Boot for managing objects/users in a City Library system. It includes user management and object management functionalities.
<br><br>
Introduction
The SuperApp Project provides an API to manage users and objects in a City Library system. It allows the creation, retrieval, update, and deletion of users, books and rooms, and supports various search functionalities. <br>
<br><br><br>
**Features:** <br>
User management (creation, retrieval, deletion) <br>
Object management (creation, retrieval, update, deletion) including add books(with permission to be an admin/librarian) to the database from the API, the user can borrow a books to their account, also there are a rooms to reserve and more services <br>
Search objects by type, alias, and alias pattern <br>
RESTful API with JSON responses<br>

**Technologies** <br>
Java <br>
Spring Boot <br>
Spring Data JPA <br>
PostgreSQL <br>
Docker <br>
Maven <br>
Swagger <br>
<br>
For the docker you should run this command: <br> 
docker-compose up --build <br>
<br><br>
after runing the project, you should open swagger from : http://localhost:8084/swagger-ui.html  <br>
and the database: http://localhost:8084/h2-console and choose postgres, and the password is "secret" <br>
<br><br><br>




this is the server-side of the project, you can download the client-side from this repositories:  <br>
<br>
the frontend developed using React.js and divided into three web applications: <br>
1- The patron application- https://github.com/yazanateer/patron-miniapp <br>
2- The librarian dashboard - https://github.com/yazanateer/librarian-app <br>
3- The room reservation - https://github.com/yazanateer/rooms-app <br>
<br>
 ****we divided the website into three websites (miniapp) as we asked in the project requirements.**



