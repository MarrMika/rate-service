# rate-service
web-application that provides an easy way to retrieve accurate and up to date currency exchange rates data.
This API is designed to be light, fast and easy to integrate.

## Requirements

* Java 11
* Maven 4 
* Docker / docker-compose

### Installation 

- use: ``git clone https://github.com/MarrMika/rate-service.git`` 
to get this app in your desired folder.
- type in terminal ``cd rate-service``
- building sources ``mvn clean install`` from project root folder

## Running App
 ```
docker run -d -p 3306:3306 --name my-mysql \
      -v ./database:/docker-entrypoint-initdb.d/ \
      -e MYSQL_ROOT_PASSWORD=root \
      -e MYSQL_USER=root \
      -e MYSQL_PASSWORD=root \
      -e MYSQL_DATABASE=rate_db \
      mysql
```
- running app `java -Dfile.encoding=UTF-8 -jar ~/rate-service/target/rate-service-1.0-SNAPSHOT-jar-with-dependencies.jar run AppVerticle -conf configs/config.json`