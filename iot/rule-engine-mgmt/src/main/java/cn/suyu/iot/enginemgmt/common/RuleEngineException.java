package cn.suyu.iot.enginemgmt.common;

import cn.suyu.iot.enginemgmt.code.RuleEngineCode;
import org.apache.commons.lang3.StringUtils;

/**
* @描述:  RuleEngineException
*
*/
public class RuleEngineException extends RuntimeException {

    protected String exceptionMessage;
    protected String errorCode;

    public RuleEngineException() {
        super();
    }

    public RuleEngineException(RuleEngineCode ruleEngineCode) {
        super(ruleEngineCode.getMessage());
        this.errorCode = ruleEngineCode.getCode();
        this.exceptionMessage = ruleEngineCode.getMessage();
    }

    public RuleEngineException(RuleEngineCode resultCode, String exceptionMessage) {
        super(exceptionMessage);
        if(StringUtils.isNotBlank(exceptionMessage)){
            this.exceptionMessage = exceptionMessage;
        }
        this.errorCode = resultCode.getCode();
    }

    public RuleEngineException(RuleEngineCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.errorCode = resultCode.getCode();
        this.exceptionMessage = resultCode.getMessage();
    }

    public RuleEngineException(String resultCode, String exceptionMessage) {
        super(exceptionMessage);
        if(StringUtils.isNotBlank(exceptionMessage)){
            this.exceptionMessage = exceptionMessage;
        }
        this.errorCode = resultCode;
    }

    /**
     * 获取异常编码
     *
     * @return
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取异常消息
     *
     * @return
     */
    public String getErrorMessage() {

        return exceptionMessage;
    }

}
