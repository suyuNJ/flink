package cn.suyu.iot.enginemgmt.enums;

/**
 * 数据转发操作类型枚举类
 */
public enum ActionTypeEnum {
    /**
     * HTTP客户端
     *
     */
    HTTP(0,"HTTP客户端"),
    /**
     * Rocketmq
     *
     */
    ROCKET_MQ(1,"Rocketmq"),
    /**
     * kafka
     *
     */
    KAFKA(2,"kafka");


    /**
     * 编码
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;


    /**
     * 私有构造函数
     * @param code
     * @param desc
     */
    ActionTypeEnum(Integer code, String desc) {

        this.code = code;

        this.desc = desc;

    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ActionTypeEnum getDataSourceEnum(Integer code){
        for (ActionTypeEnum dataSourceEnum : ActionTypeEnum.values()) {
            if(dataSourceEnum.code.intValue() == code) {
                return dataSourceEnum;
            }
        }
        return null;
    }


}
