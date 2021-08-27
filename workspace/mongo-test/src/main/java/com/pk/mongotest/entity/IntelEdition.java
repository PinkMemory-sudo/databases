package com.pk.mongotest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 * 最新情报版本信息Model
 *
 * @author Kipeng Huang <huangpeng08@baidu.com>
 * @since 2021/5/24 5:15 下午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntelEdition {
    /**
     * 版本名称
     */
    private String name;
    /**
     * 开始时间
     * 秒级时间戳
     */
    private long startTime;
    /**
     * 结束时间
     * 秒级时间戳
     */
    private long endTime;
    /**
     * 推送日期
     */
    private List<DayOfWeek> pushDate;
    /**
     * 这个版本包含了哪些二级意图，以及对应的分数限制
     */
    private Map<String, Double> scoreLimit;
    /**
     * 单次推送数量
     */
    private int pushCount;
}
