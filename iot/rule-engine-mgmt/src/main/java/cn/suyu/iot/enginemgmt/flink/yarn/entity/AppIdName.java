package cn.suyu.iot.enginemgmt.flink.yarn.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** @Description 记录app name和对应的app Id */
@Data
@Builder
@AllArgsConstructor
public class AppIdName {
  private String appName;
  private String appId;
}
