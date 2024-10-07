# Real-time-ETL
## To run : 
You will need scala 2 installed, checkout the dependencies in [build.sbt](kafka-streams/build.sbt)

```bash
docker compose up -d
```
navigate to `/kafka-streams` directory and install dependencies :
```bash
sbt update
```

run every component of the pipeline as follows : 
```bash
sbt "runMain producer.Producer"
sbt "runMain kStream.Kstream"
sbt "runMain consumer.Consumer"
```


