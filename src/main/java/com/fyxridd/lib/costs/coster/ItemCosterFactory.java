package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.costs.api.model.CosterFactory;
import com.fyxridd.lib.items.api.ItemsApi;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class ItemCosterFactory implements CosterFactory<ItemCoster>{
    public static final String KEY = "item";

    @Override
    public ItemCoster produce(ConfigurationSection cs) {
        Map<Integer, ItemCoster.ItemCostInfo> items = new HashMap<>();

        int index = 0;
        while (true) {
            String path = "values."+(++index);
            if (!cs.contains(path)) break;
            ConfigurationSection cs2 = cs.getConfigurationSection(path);
            {
                boolean exact = cs2.contains("exact");
                int amount = cs2.getInt("amount", 1);
                ItemCoster.ItemCostInfo itemCostInfo = exact?
                        new ItemCoster.ItemCostInfo(amount, ItemsApi.loadItemStack(cs2.getConfigurationSection("exact"))):
                        new ItemCoster.ItemCostInfo(amount, CoreApi.getMaterial(cs2.getString("normal.type")), cs2.getInt("normal.smallId"));
                items.put(index, itemCostInfo);
            }
        }

        return new ItemCoster(items);
    }
}
