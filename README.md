### REST Api on WebFlux

This application demonstrates a simple rest api service.
It is a simple logging system for sending files to the cloud with different levels of access.

To use the project, you can copy the project to yourself by downloading the archive and unzip it to the folder you need or do the git clone command.

To work you will need a MYSQL database.
Access to the Yandex S3 cloud, passwords and bucket to send files there.

The project has two profiles, one main and one test, carefully fill in yaml files.
The test profile uses test migrations to the test database as well as a test bucket for sending files.

Before launching the project, I recommend running integration tests and unit tests.

The project uses securrency technologies with the help of jwttoken.
As well as several access levels with different capabilities.

> ADMIN - full access to the application
MODERATOR - USER rights + read all Users + read/modify/change/delete all Events + read/modify/delete all Files
USER - only read all its data + download files for itself
> 

For users with user access level there is uri, which allows to pass your jwt token after login and reflect data, as well as get all current information on yourself.
