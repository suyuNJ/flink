package cn.suyu.iot.enginemgmt.hadoop;

import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;

/**
 * @Description HDFSClient
 */
@Slf4j
@Component
public class HDFSClient {
    private static String HDFS_URI;

    @Value("${flink.hdfs.address}")
    public void setHdfsUrl(String hdfsUrl) {
        HDFS_URI = hdfsUrl;
    }

    /**
     * @param localFName 本地文件
     * @param hdfsFName  hdfs系统下文件
     * @throws IOException
     */
    public static void uploadFileFromLocation(String localFName, String hdfsFName) {

        log.info("upload file from location to hdfs. localFName: {}, hdfsFName: {}", localFName, hdfsFName);
        String hdfsURI = HDFS_URI + "/" + hdfsFName;
        Configuration conf = new Configuration();  // 定义conf对象
        // 云端HDFS文件路径 user/hadoop
        try (
                InputStream in = new BufferedInputStream(new FileInputStream(localFName));
                FileSystem fs = FileSystem.get(URI.create(hdfsURI), conf);  // 创建文件系统 对象
                // 输出流
                OutputStream out = fs.create(new Path(hdfsURI), () -> log.info("上传完成一个文件到HDFS"));
        ) {
            IOUtils.copyBytes(in, out, 1024, true);  // 连接两个流，形成通道，使输入流向输出流传输数据
            out.flush();
        } catch (IOException e) {
            Log.error("hdfs upload error.", e);
        }
    }

    public static void uploadFileFromInputStream(InputStream in, String hdfsFName) {
        log.info("upload file from inputStream to hdfs. hdfsFName: {}", hdfsFName);
        String hdfsURI = HDFS_URI + "/" + hdfsFName;
        Configuration conf = new Configuration();  // 定义conf对象
        // 云端HDFS文件路径 user/hadoop
        try (
                FileSystem fs = FileSystem.get(URI.create(hdfsURI), conf);  // 创建文件系统 对象
                // 输出流
                OutputStream out = fs.create(new Path(hdfsURI), () -> log.info("上传完成一个文件到HDFS"));
        ) {
            IOUtils.copyBytes(in, out, 1024, true);  // 连接两个流，形成通道，使输入流向输出流传输数据
            out.flush();
        } catch (IOException e) {
            Log.error("hdfs upload error.", e);
        }
    }

    public static void processDependFiles(String dependFileDir) {
        int index = dependFileDir.lastIndexOf(File.separator);
        String jarFileName = dependFileDir.substring(index + 1);
        HDFSClient.uploadFileFromLocation(dependFileDir, jarFileName);
    }
}
