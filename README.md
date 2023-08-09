# Zoomer

This project is a "mock" version of the Uber back-end API.

In this project, I utilized **Java** and **Dagger2 dependency injection framework** to build a an array of microservices
* [Users](UserMicroservice/) microservice that manages the users of the application -> utilized **PostgreSQL** as database
* [TripInfo](TripinfoMicroservice/) microservice that manages methods related to booking a trip -> utilized **MongoDB** as database
* [Location Service](locationmicroservice/) microservice that manages methods related to locations between different users -> utilized **Neo4j** as database

Finally, an [Api Gateway](ApiGateway/) was used to direct requests to different microservices.
