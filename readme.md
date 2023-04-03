```
./gradlew clean bootBuildImage
docker-compose up -d
docker run --network=host --rm docker.io/library/r2dbc-migrate-native:0.0.1-SNAPSHOT
```
