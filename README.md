# Real-time-ETL
Real time ETL Pipeline in kafka Streams and Scala in a microservice architecture, Running on kubernetes
## To run : 
You will need scala 2 installed, checkout the dependencies in [build.sbt](kafka-streams/build.sbt)
Clone and then Deploy the Pipeline in your cluster :
```bash
k apply -f ./k8s
```
