package cn.suyu.iot.enginemgmt.flink.yarn.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** @Description 记录job name和对应的job Id */
@Data
@Builder
@AllArgsConstructor
public class JobIdName {
  private String jobName;
  private String jobId;
}
