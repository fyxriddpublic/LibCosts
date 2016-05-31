package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.core.api.EcoApi;
import com.fyxridd.lib.costs.CostsPlugin;
import com.fyxridd.lib.costs.api.model.CheckCostResult;
import com.fyxridd.lib.costs.api.model.CostResult;
import com.fyxridd.lib.costs.api.model.Coster;
import org.bukkit.entity.Player;

public class MoneyCoster implements Coster{
    private int cost;

    public MoneyCoster(int cost) {
        this.cost = cost;
    }

    @Override
    public CheckCostResult checkCost(String plugin, Player p) {
        boolean result = true;
        if (cost > 0) {
            if ((int) EcoApi.get(p.getName()) < cost) result = false;
        }
        String tmp = CostsPlugin.instance.getCostsManager().get(p.getName(), result?1330:1340).getText();
        String tip = CostsPlugin.instance.getCostsManager().get(p.getName(), 2000, cost, tmp).getText();
        return new CheckCostResult(result, tip);
    }

    @Override
    public CostResult cost(String plugin, Player p) {
        if (cost <= 0) return CostResult.success;
        int hasMoney = (int) EcoApi.get(p.getName());
        if (hasMoney > 0) {
            if (hasMoney >= cost) {
                EcoApi.del(p.getName(), cost);
                return CostResult.success;
            }else {
                EcoApi.set(p.getName(), 0);
                return CostResult.partSuccess;
            }
        }else return CostResult.fail;
    }
}
