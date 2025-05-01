Clean build
```sh
mvn clean install -DskipTests
```

Run Spring Boot app in development mode
```sh
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```


# Reference
Serialize/Deserialize JSON/JSONB type: https://github.com/hantsy/spring-r2dbc-sample/blob/master/docs/pg.md