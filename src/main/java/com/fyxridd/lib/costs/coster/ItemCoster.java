package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.core.api.ItemApi;
import com.fyxridd.lib.costs.CostsPlugin;
import com.fyxridd.lib.costs.api.model.CheckCostResult;
import com.fyxridd.lib.costs.api.model.CostResult;
import com.fyxridd.lib.costs.api.model.Coster;
import com.fyxridd.lib.names.api.NamesApi;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemCoster implements Coster{
    public static class ItemCostInfo {
        private boolean exact;
        private int amount;

        //exact == true
        private ItemStack is;

        //exact == false
        private Material type;
        private int smallId;

        public ItemCostInfo(int amount, ItemStack is) {
            this.exact = true;
            this.amount = amount;

            this.is = is;
        }

        public ItemCostInfo(int amount, Material type, int smallId) {
            this.exact = false;
            this.amount = amount;

            this.type = type;
            this.smallId = smallId;
        }

        public boolean isExact() {
            return exact;
        }

        public int getAmount() {
            return amount;
        }

        public ItemStack getIs() {
            return is;
        }

        public Material getType() {
            return type;
        }

        public int getSmallId() {
            return smallId;
        }
    }

    private Map<Integer, ItemCostInfo> items;

    public ItemCoster(Map<Integer, ItemCostInfo> items) {
        this.items = items;
    }

    @Override
    public CheckCostResult checkCost(String plugin, Player p) {
        boolean result = true;
        List<String> tips = new ArrayList<>();
        if (items != null) {
            Inventory inv = p.getInventory();
            for (int index=1;index<=items.size();index++) {
                ItemCostInfo itemCostInfo = items.get(index);
                if (itemCostInfo != null) {
                    boolean flag;
                    if (itemCostInfo.isExact()) flag = ItemApi.hasExactItem(inv, itemCostInfo.getIs(), itemCostInfo.getAmount(), true);
                    else flag = ItemApi.hasNormalItem(inv, itemCostInfo.getType(), itemCostInfo.getSmallId(), itemCostInfo.getAmount());

                    String tmp = CostsPlugin.instance.getCostsManager().get(p.getName(), flag?1330:1340).getText();
                    String itemName = itemCostInfo.isExact()?NamesApi.getItemName(itemCostInfo.getIs()):NamesApi.getItemName(itemCostInfo.getType().getId(), itemCostInfo.getSmallId());
                    tips.add(CostsPlugin.instance.getCostsManager().get(p.getName(), 2030, itemName, itemCostInfo.getAmount(), tmp).getText());

                    if (!flag) result = false;
                }
            }
        }
        return new CheckCostResult(result, tips);
    }

    @Override
    public CostResult cost(String plugin, Player p) {
        if (items == null || items.isEmpty()) return CostResult.success;

        boolean result = true;
        boolean noneFlag = false;//true表示至少是部分减少成功的
        Inventory inv = p.getInventory();
        for (int index=1;index<=items.size();index++) {
            ItemCostInfo itemCostInfo = items.get(index);
            if (itemCostInfo != null) {
                boolean flag;
                if (itemCostInfo.isExact()) {
                    if (!noneFlag && ItemApi.hasExactItem(inv, itemCostInfo.getIs(), 1, true)) noneFlag = true;
                    flag = ItemApi.removeExactItem(inv, itemCostInfo.getIs(), itemCostInfo.getAmount(), true, true);
                }else {
                    if (!noneFlag && ItemApi.hasNormalItem(inv, itemCostInfo.getType(), itemCostInfo.getSmallId(), 1)) noneFlag = true;
                    flag = ItemApi.removeNormalItem(inv, itemCostInfo.getType(), itemCostInfo.getSmallId(), itemCostInfo.getAmount(), true);
                }
                if (!flag) result = false;
            }
        }

        if (result) return CostResult.success;
        else if (noneFlag) return CostResult.partSuccess;
        else return CostResult.fail;
    }
}
