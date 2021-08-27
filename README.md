# flink
rule engine project

1、dataSet from file
read hello.txt
2、dataStream from socket
linux server：nc -lk 7777
java Program arguments：--host ××.××.××.×× --port ××
3、dataStream from kafka
./bin/kafka-server-start.sh -daemon ./config/server.properties
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic sensor
