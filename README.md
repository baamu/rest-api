![banner](https://raw.githubusercontent.com/baamu/rest-api/master/resources/Banner.png)

# NightWolf Download Manager RESTful API

## Dependencies
1) MySQL
2) Maven (optional)

## Configuration
1) Clone the repository
2) Create a mysql database for the project (ex : resttest)
3) Open "dbScript.sql" and edit the default_paths in the following insert queries as the downloads will be mapped to these paths :
    > Note : Provide absolute paths in your system for the default download paths
    
    ```mysql
        INSERT INTO download_type (file_type, default_path) VALUES (
          "documents",
          "C:\\Users\\abc\\Desktop\\Downloads\\Documents"
        ),
        (
          "images",
          "C:\\Users\\abc\\Desktop\\Downloads\\Pictures"
        ),
        (
          "audios",
          "C:\\Users\\abc\\Desktop\\Downloads\\Audios"
        ),
        (
          "videos",
          "C:\\Users\\abc\\Desktop\\Downloads\\Videos"
        ),
        (
          "programs",
          "C:\\Users\\abc\\Desktop\\Downloads\\Programs"
        ),
        (
          "other",
          "C:\\Users\\abc\\Desktop\\Downloads\\Other"
        );
    ```
4) Import database using "dbScript.sql"
5) Navigate to src/main/resources/application.properties and edit the following configurations
    1) server.port (default is 8080)
    2) spring.datasource.url (as per the database)
    3) spring.datasource.username (mysql username)
    4) spring.datasource.password (mysql password)
    5) spring.mail.username (email account)
    6) spring.mail.password (email account password) 
6) Navigate to src/main/java/io/github/nightwolf/restapi/security/SecurityConstants.java and edit the following constants :
    1) SECRET (The secret key to encrypt JWT token)
    2) EXPIRATION_TIME (Token expiration time - default 10 days)
    3) FILE_DOWNLOAD_PATH (downloads will be saved in this path - give an absolute path)

> Note : These configurations will be changed as the development goes on, thus not final

## Executing the project
* Execute using maven wrapper :
1) Navigate to the source directory
2) Open terminal/cmd and run the project using:
    ```./mvnw spring-boot:run```

* Or build it from source using maven and execute the package jar file

## Default user credentials :
    | Email                 | Password  | Role  |
    |:----------------------|:----------|:------|
    | admin@nightwolf.com   | admin     | Admin |
    | user@nightwolf.com    | user      | user  |

> Note : passwords are hashed using bcrypt encoding


## TODO :
1) Implement repository search per client request ![alt](https://img.shields.io/badge/Priority-High-Red?style=flat-square) ![alt](https://img.shields.io/badge/State-Fixed-Blue?style=flat-square)
2) Implement copy files from repository ![alt](https://img.shields.io/badge/Priority-High-Red?style=flat-square)
3) Save added downloads in temp_download ![alt](https://img.shields.io/badge/Priority-Moderate-orange?style=flat-square) ![alt](https://img.shields.io/badge/State-On_Going-Green?style=flat-square)
4) Fix setting download paths in a different property file ![alt](https://img.shields.io/badge/Priority-Moderate-orange?style=flat-square) ![alt](https://img.shields.io/badge/State-Fixed-Blue?style=flat-square)
5) Implement notifying after each download finishes and move the data to download table from temp_download ![alt](https://img.shields.io/badge/Priority-Moderate-orange?style=flat-square)
6) Implement downloading to the proper directory based on the file type ![alt](https://img.shields.io/badge/Priority-Low-yellow?style=flat-square) ![alt](https://img.shields.io/badge/State-Fixed-Blue?style=flat-square)
7) Add repository check/clean scheduling ![alt](https://img.shields.io/badge/Priority-Low-yellow?style=flat-square) ![alt](https://img.shields.io/badge/State-On_Going-Green?style=flat-square)
8) Test against incoming new download requests to the queue (while downloads has started) ![alt](https://img.shields.io/badge/Priority-Low-yellow?style=flat-square) ![alt](https://img.shields.io/badge/State-On_Going-Green?style=flat-square)
9) Test resuming paused downloads ![alt](https://img.shields.io/badge/Priority-Low-yellow?style=flat-square) ![alt](https://img.shields.io/badge/State-On_Going-Green?style=flat-square)
10) Fix property file reading ![alt](https://img.shields.io/badge/Priority-Low-yellow?style=flat-square)

