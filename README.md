# ❗ Current state ❗
For now project have reboot state, i`m rebuilding all logic and structure from very begining to implement new knowledges and skills. 
Current state can be viewed at [VolunNearApp/mvp-reboot](https://github.com/sudo-mvrk/VolunNearApp/tree/mvp-reboot) branch.

Of course, you can run the old version by following the steps below.

# VolunNearApp
**VolunNearApp** where people can search for a place where they want to volunteer based on their location and wishes (kind of activity), and volunteer organizations can “post” their places where people can to volunteer.

## API`s endpoints can be seen at Swagger
After building VolunNearApp go to, all endpoints of API at that link: http://localhost:8080/swagger-ui/index.html

## To build the project
1. Create a package (JAR) of project (from root folder):
   ```bash
   mvn package
   ```
3. After that start (also root folder):
   ```bash
   docker compose up
   ```
4. For api docs go to http://localhost:8080/swagger-ui/index.html
5. For work use HTTP requests as an example you can use Postman
## For build from application-dev
For build from dev profile (this is the main one for now) env variables are required:
| Name | Value |
|--|--|
|DB_URL| DB url which matches with docker DB url (docker-compose.yml)
|DB_USERNAME|In application-dev.properties only|
| DB_PASSWORD| Password for db must match with password DB in docker-compose.yml |
|EMAIL_USERNAME| Email usernmae for smtp login with google account |
|EMAIL_PASSWORD| Password for application from google account ([more info](https://support.google.com/accounts/answer/185833?hl=en)) |
