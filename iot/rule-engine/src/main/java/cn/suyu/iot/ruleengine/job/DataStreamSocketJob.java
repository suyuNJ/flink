package cn.suyu.iot.ruleengine.job;

import cn.suyu.iot.ruleengine.function.WordCountMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Description 开启一个本地9999的TCP协议端口，由客户端主动发起连接，强制服务器保持连接并继续监听端口
 * @Author suyu
 * @Data 2020/11/7 16:22
 */
public class DataStreamSocketJob {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //读取配置
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String host = parameterTool.get("host");
        int port = parameterTool.getInt("port");
        //设置并行度
        env.setParallelism(1);

        //从文件读取数据
        DataStreamSource<String> inputDataStream = env.socketTextStream(host, port);

        //对数据进行转换处理统计，先分词，再按照word进行分组，最后聚合统计
        //方法一
//        DataStream<Tuple2<String, Integer>> dataStream = inputDataStream.flatMap(new FlatMapFunction<String, String>() {
//            public void flatMap(String s, final Collector<String> collector) throws Exception {
//                final String[] words = s.split(" ");
//                for (String str : words) {
//                    collector.collect(str);
//                }
//            }
//        }).filter(new FilterFunction<String>() {
//            public boolean filter(String s) throws Exception {
//                return !StringUtils.isEmpty(s);
//            }
//        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
//
//            public Tuple2<String, Integer> map(String s) throws Exception {
//                Tuple2<String, Integer> countTuple = new Tuple2<String, Integer>();
//                countTuple.setFields(s, 1);
//                return countTuple;
//            }
//        }).keyBy(0).sum(1);

        //方法二
        DataStream<Tuple2<String, Integer>> dataStream = inputDataStream.flatMap(new WordCountMapFunction()).keyBy(0).sum(1);

        dataStream.print();

        env.execute();

    }
}
