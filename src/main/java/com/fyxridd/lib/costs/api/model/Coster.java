package com.fyxridd.lib.costs.api.model;

import org.bukkit.entity.Player;

/**
 * 花费器
 */
public interface Coster {
    /**
     * 检测花费
     * @return 检测花费结果
     */
    CheckCostResult checkCost(String plugin, Player p);

    /**
     * (强制)花费
     * @return 花费结果
     */
    CostResult cost(String plugin, Player p);
}
