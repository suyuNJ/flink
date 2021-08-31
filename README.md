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
4、deploy mode
flink on yarn
4.1 session mode
   (1) start session
   ./bin/yarn-session.sh -n 2 -s 2 -jm 512m -tm 512m
   tips:-n(the numbers of taskmanager) is deprecated
   ./bin/yarn-session.sh  -jm 816  -tm  2048 -nm cuiot-v1 -d
   (2) start job
   /bin/flink run -yid application_1607912082840_0001  -C "hdfs://node1:8020/MyPattern1681607494377361.jar" examples/streaming/cuiot-rule-cep-0.8.0.jar
   ./bin/flink run -c cn.suyu.iot.ruleengine.job.DataStreamSocketJob /data/suyu/rule-engine-1.0-SNAPSHOT.jar --host 172.30.125.50 --port 7777
4.2 per-job mode
   ./bin/flink run -m yarn-cluster \
                                  ./examples/batch/WordCount.jar \
                                  --input hdfs:///user/hamlet.txt --output hdfs:///user/wordcount_out
   ./bin/flink run -m yarn-cluster -c cn.suyu.iot.ruleengine.job.DataStreamSocketJob /data/suyu/rule-engine-1.0-SNAPSHOT.jar --host 172.30.125.50 --port 7777

