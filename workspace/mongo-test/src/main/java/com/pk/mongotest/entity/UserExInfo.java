package com.pk.mongotest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

/**
 * 用户补充信息
 *
 * @author Kipeng Huang <huangpeng08@baidu.com>
 * @since 2021/5/24 5:02 下午
 */
@Data
@Document(collection = "user_ex_info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserExInfo {
    /**
     * 关联用户表accountName
     */
    @Id
    private String id;
    /**
     * 警种
     */

    private String policeType;
    /**
     * 最新情报配置信息
     */
    private List<IntelEdition> intelEditions;
    /**
     * 智能管控开始时间
     * 秒级时间戳
     */
    private long warnStartTime;
    /**
     * 智能管控结束时间
     * 秒级时间戳
     */
    private long warnEndTime;
    /**
     * 疑似甄别开始时间
     * 秒级时间戳
     */
    private long suspectStartTime;
    /**
     * 疑似甄别结束时间
     * 秒级时间戳
     */
    private long suspectEndTime;
    /**
     * 账号到期时间
     * 秒级时间戳
     * 账号下开通服务的最晚结束时间
     */
    @Indexed
    private long endTime;
    /**
     * 账号状态
     * 默认启用
     */
    private AccountStatus status = AccountStatus.ENABLED;
    /**
     * 历史开通的功能
     * 用于用户数据查询
     * 这个字段功能只增不减
     */
    private Set<Function> functions;

    /**
     * 账户的有效期为各服务的最晚时间
     *
     * @param endTime
     */
    public void updateEndTime(long endTime) {
        if (this.endTime < endTime) {
            this.endTime = endTime;
        }
    }


}
