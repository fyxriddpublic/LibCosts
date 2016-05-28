package com.fyxridd.lib.costs.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测花费结果
 */
public class CheckCostResult{
    //检测花费是否成功(花费是否满足)
    private boolean success;
    //提示,可为null或空表示不提示
    private List<String> tips;

    public CheckCostResult(boolean success, List<String> tips) {
        this.success = success;
        this.tips = tips;
    }

    public CheckCostResult(boolean success, String tip) {
        this.success = success;
        this.tips = new ArrayList<>();
        this.tips.add(tip);
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getTips() {
        return tips;
    }
}
