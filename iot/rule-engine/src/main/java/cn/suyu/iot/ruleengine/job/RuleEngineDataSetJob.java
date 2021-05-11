package cn.suyu.iot.ruleengine.job;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @Description 读取指定路径文件作为输入源，做批处理
 * @Author suyu
 * @Data 2020/11/7 16:22
 */
public class RuleEngineDataSetJob {
    public static void main(String[] args) throws Exception {
        //创建执行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //从文件读取数据
        String inputPath = "D:\\dev\\src\\mygithub\\flink\\iot\\rule-engine\\src\\main\\resources\\hello.txt";
        DataSource<String> inputDataSource = env.readTextFile(inputPath);

        //对数据进行转换处理统计，先分词，再按照word进行分组，最后聚合统计
        DataSet<Tuple2<String, Integer>> dataSet = inputDataSource.flatMap(new FlatMapFunction<String, String>() {
            public void flatMap(String s, final Collector<String> collector) throws Exception {
                final String[] words = s.split(" ");
                for (String str : words) {
                    collector.collect(str);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {

            public Tuple2<String, Integer> map(String s) throws Exception {
                Tuple2<String, Integer> countTuple = new Tuple2<String, Integer>();
                countTuple.setFields(s, 1);
                return countTuple;
            }
        }).groupBy(0).sum(1);

        dataSet.print();


    }
}
