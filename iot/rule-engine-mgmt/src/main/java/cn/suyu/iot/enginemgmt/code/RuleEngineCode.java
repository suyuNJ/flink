package cn.suyu.iot.enginemgmt.code;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum RuleEngineCode {

    GET_APPLICATION_ERROR_CODE("130126", "获取Application错误"),
    GET_JOB_ERROR_CODE("130127", "获取Job错误"),
    NO_RUNNING_APPLICATIONS("130128", "没有running的applications"),
    EXEC_ARGS_CANNOT_EMPTY("130129", "execArgs不能为empty"),
    GET_FLINK_RUN_JAR_FAILS("130131", "获取flink运行jar包错误"),
    CANNOT_GET_RUNNING_STATUS_JOB("130137", "根据请求信息无法获取running Job"),
    CANNOT_GET_JOB_ID("130138", "根据请求信息无法获取Job ID"),
    CANNOT_GET_JOB_NAME("130139", "根据请求信息无法获取Job Name"),
    CANNOT_GET_APPLICATION_NAME("130140", "根据请求信息无法获取App Name"),
    GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE("130141", "根据appname获取的appId不唯一则报错"),
    JOB_NAME_EMPTY("130142", "Job Name为空"),
    FLINK_JOB_RESPONSE_EMPTY("130143", "flinkJobResponseBodyApiResponse为空"),
    FLINK_JOB_RESPONSE_STATUS_ERROR("130144", "flinkJobResponseBodyApiResponse status异常"),
    FLINK_JOB_RESPONSE_DATA_EMPTY("130145", "flinkJobResponseBodyApiResponse data为空"),
    APP_NAME_EMPTY("130146", "App Name为空"),
    APP_ID_EMPTY("130147", "App Id为空"),
    FLINK_APPLICATION_RESPONSE_EMPTY("130148", "applicationResponseBodyApiResponse为空"),
    FLINK_APPLICATION_RESPONSE_STATUS_ERROR("130149", "applicationResponseBodyApiResponse status异常"),
    FLINK_APPLICATION_RESPONSE_DATA_EMPTY("130150", "applicationResponseBodyApiResponse data为空");
    /**
     * 状态码
     */
    private String code;

    /**
     * 状态信息
     */
    private String message;

    /**
     * 私有构造函数
     *
     * @param code    状态码
     * @param message 状态信息
     * @return
     */
    private RuleEngineCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
