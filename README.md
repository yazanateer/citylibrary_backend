citylibrary Project - library management system 
This project is a RESTful API built with Java Spring Boot for managing objects/users in a City Library system. It includes user management and object management functionalities.

Introduction
The SuperApp Project provides an API to manage users and objects in a City Library system. It allows the creation, retrieval, update, and deletion of users, books and rooms, and supports various search functionalities.

Features:
User management (creation, retrieval, deletion)
Object management (creation, retrieval, update, deletion) including add books(with permission to be an admin/librarian) to the database from the API, the user can borrow a books to their account, also there are a rooms to reserve and more services
Search objects by type, alias, and alias pattern
RESTful API with JSON responses

Technologies
Java
Spring Boot
Spring Data JPA
PostgreSQL
Docker
Maven
Swagger

For the docker you should run this command: 
docker-compose up --build

after runing the project, you should open swagger from : http://localhost:8084/swagger-ui.html 
and the database: http://localhost:8084/h2-console and choose postgres, and the password is "secret"





this is the server-side of the project, you can download the client-side from this repositories: 

the frontend developed using React.js and divided into three web applications:
1- The patron application- https://github.com/yazanateer/patron-miniapp
2- The librarian dashboard - https://github.com/yazanateer/librarian-app
3- The room reservation - https://github.com/yazanateer/rooms-app

** we divided the website into three websites (miniapp) as we asked in the project requirements.



