# How To Run Tests

Before starting you need to:
- [Create .env file](./create-env.md)

## Run docker

```bash
docker compose -f .\docker-compose.test.yml up --build
```

`Optional`:
```bash
docker compose -p testenv -f .\docker-compose.test.yml up --build
```
