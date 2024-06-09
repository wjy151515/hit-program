package com.demo.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * UpdateCache 类用于缓存每个医生的最后查询时间戳。
 * 该类使用一个 HashMap 来存储医生 ID 和对应的最后查询时间戳。
 * 它提供了获取和设置最后查询时间的方法。
 */
@Component
public class UpdateCache {

    // 存储医生 ID 和对应最后查询时间戳的 Map
    private Map<Integer, Long> lastQueryTimes = new HashMap<>();

    /**
     * 获取指定医生的最后查询时间戳。
     *
     * @param doctorId 医生的 ID
     * @return 最后查询时间戳，如果不存在则返回 null
     */
    public Long getLastQueryTime(int doctorId) {
        return lastQueryTimes.get(doctorId);
    }

    /**
     * 设置指定医生的最后查询时间戳。
     *
     * @param doctorId      医生的 ID
     * @param lastQueryTime 最后查询时间戳
     */
    public void setLastQueryTime(int doctorId, long lastQueryTime) {
        lastQueryTimes.put(doctorId, lastQueryTime);
    }
}
