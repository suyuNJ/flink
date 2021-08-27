package cn.suyu.iot.ruleengine.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @Description TODO
 * @Author suyu
 * @Data 2021/8/27 15:38
 */
public class WordCountMapFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
        String[] words = value.split(" ");
        for (String word : words) {
            out.collect(new Tuple2<>(word, 1));
        }
    }
}
