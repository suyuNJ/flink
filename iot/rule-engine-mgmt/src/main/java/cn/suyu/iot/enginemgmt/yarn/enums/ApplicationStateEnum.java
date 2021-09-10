package cn.suyu.iot.enginemgmt.yarn.enums;

/**
 * session状态（state）
 */
public enum ApplicationStateEnum {

    /**
     * 运行
     */
    RUNNING("RUNNING","运行"),

    /**
     * 完成
     */
    FINISHED("FINISHED","完成"),

    /**
     * 失败
     */
    FAILED("FAILED","失败"),

    /**
     * 取消
     */
    KILLED("KILLED","取消");


    /**
     * 编号
     */
    private String code;

    /**
     * 描述
     */
    private String desc;


    /**
     * 私有构造函数
     * @param code
     * @param desc
     */
    ApplicationStateEnum(String code, String desc) {

        this.code = code;

        this.desc = desc;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ApplicationStateEnum getDataSourceEnum(Integer code){
        for (ApplicationStateEnum dataSourceEnum : ApplicationStateEnum.values()) {
            if(dataSourceEnum.code.equals(code)) {
                return dataSourceEnum;
            }
        }
        return null;
    }


}
