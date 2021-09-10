/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.suyu.iot.enginemgmt.flink.yarn.enums;

import cn.suyu.iot.enginemgmt.enums.ActionTypeEnum;

/**
 * @Description flink yarn 启动job shell脚本类型
 */
public enum RuleJobShellType {
    /* 数据转发 **/
    FORWORD_RULE(0,"数据转发"),

    /* 场景联动 **/
    SCENE_LINKAGE(1,"场景联动");



    private Integer code;

    private String desc;


    /**
     * 私有构造函数
     * @param code
     * @param desc
     */
    RuleJobShellType(Integer code, String desc) {

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
            if(dataSourceEnum.getCode() == code) {
                return dataSourceEnum;
            }
        }
        return null;
    }

}
