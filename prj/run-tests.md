# How to run test

```bash
mvn clean package -DskipTests=true
docker compose down
docker compose -f .\docker-compose.test.yml up --build
```