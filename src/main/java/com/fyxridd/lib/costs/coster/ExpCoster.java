package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.costs.CostsPlugin;
import com.fyxridd.lib.costs.api.model.CheckCostResult;
import com.fyxridd.lib.costs.api.model.CostResult;
import com.fyxridd.lib.costs.api.model.Coster;
import org.bukkit.entity.Player;

public class ExpCoster implements Coster{
    private int cost;

    public ExpCoster(int cost) {
        this.cost = cost;
    }

    @Override
    public CheckCostResult checkCost(String plugin, Player p) {
        boolean result = true;
        if (cost > 0) {
            int has = p.getTotalExperience();
            if (has < cost) result = false;
        }
        String tmp = CostsPlugin.instance.getCostsManager().get(p.getName(), result?1330:1340).getText();
        String tip = CostsPlugin.instance.getCostsManager().get(p.getName(), 2010, cost, tmp).getText();
        return new CheckCostResult(result, tip);
    }

    @Override
    public CostResult cost(String plugin, Player p) {
        if (cost <= 0) return CostResult.success;
        int has = p.getTotalExperience();
        if (has > 0) {
            if (has >= cost) {
                p.setTotalExperience(has-cost);
                return CostResult.success;
            }else {
                p.setTotalExperience(0);
                return CostResult.partSuccess;
            }
        }else return CostResult.fail;
    }
}
