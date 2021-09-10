package cn.suyu.iot.enginemgmt.common.asserts;

import cn.suyu.iot.enginemgmt.common.RuleEngineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @Description AbstractRuleEngineAssert
 */
public abstract class AbstractRuleEngineAssert {

    public static void notNull(Object object, String code, String message) {
        if (object == null) {
            throw new RuleEngineException(code, message);
        }
    }

    public static void isNull(Object object, String code, String message) {
        if (object != null) {
            throw new RuleEngineException(code, message);
        }
    }

    public static void notEmpty(String str, String code, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new RuleEngineException(code, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String code, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new RuleEngineException(code, message);
        }
    }

    public static void isTrue(boolean expression, String code, String message) {
        if (!expression) {
            throw new RuleEngineException(code, message);
        }
    }

    public static void isFalse(boolean expression, String code, String message) {
        if (expression) {
            throw new RuleEngineException(code, message);
        }
    }
}
