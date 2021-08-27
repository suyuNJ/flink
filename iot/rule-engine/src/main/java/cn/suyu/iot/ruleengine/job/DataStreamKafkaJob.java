package cn.suyu.iot.ruleengine.job;

import cn.suyu.iot.ruleengine.function.WordCountMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * @Description source为kafka的流式处理
 * @Author suyu
 * @Data 2020/11/7 16:22
 */
public class DataStreamKafkaJob {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "*.*.*.*:9092");
        properties.setProperty("group.id", "consumer.group");

        //从kafka读取数据
        DataStreamSource<String> dataStreamSource = env.addSource(new FlinkKafkaConsumer<String>("sensor", new SimpleStringSchema(), properties));

        //方法二
        DataStream<Tuple2<String, Integer>> dataStream = dataStreamSource.flatMap(new WordCountMapFunction()).keyBy(0).sum(1);

        dataStream.print();

        env.execute();

    }
}
