# How to Build and Run Application

Before starting you need to:
- [Create .env file](./create-env.md)

## Build project without running tests

```bash
mvn clean package -DskipTests=true
```

## Run docker

```bash
docker compose up --build
```
