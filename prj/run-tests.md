# How to run test

Env example
```.env
POSTGRES_DB=dbname
POSTGRES_USER=postgresiks
POSTGRES_PASSWORD=1234aue

SPRING_DATASOURCE_URL_TEST=jdbc:postgresql://postgres:5432/event_finder/test
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/event_finder
SPRING_DATASOURCE_USERNAME=postgresiks
SPRING_DATASOURCE_PASSWORD=1234aue
JWT_SECRET=qweqweqweqweqweqweqweqweqweqweqweqweqwe
```

```bash
mvn clean package -DskipTests=true; docker compose down; docker compose -p testenv -f .\docker-compose.test.yml up --build
```
