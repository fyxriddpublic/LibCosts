package com.fyxridd.lib.costs.manager;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.EcoApi;
import com.fyxridd.lib.core.api.ItemApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.costs.CostsPlugin;
import com.fyxridd.lib.costs.config.LangConfig;
import com.fyxridd.lib.costs.model.CostInfo;
import com.fyxridd.lib.items.api.ItemsApi;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostsManager {
    private LangConfig langConfig;

    //插件名 类型名 花费信息
    private Map<String, HashMap<String, CostInfo>> costsHash = new HashMap<>();

	public CostsManager() {
        //添加配置监听
        ConfigApi.addListener(CostsPlugin.instance.pn, LangConfig.class, new Setter<LangConfig>() {
            @Override
            public void set(LangConfig value) {
                langConfig = value;
            }
        });
	}

    /**
     * @see com.fyxridd.lib.costs.api.CostsApi#cost(Player, String, String, boolean, boolean)
     */
    public boolean cost(Player p, String plugin, String type, boolean force, boolean tip){
        if (p == null || plugin == null || type == null) return false;

        List<FancyMessage> tips = new ArrayList<>();

        //类型不存在
        HashMap<String, CostInfo> hash = costsHash.get(plugin);
        if (hash == null) {
            if (tip) ShowApi.tip(p, get(1320), true);
            return false;
        }
        CostInfo costInfo = hash.get(type);
        if (costInfo == null) {
            if (tip) ShowApi.tip(p, get(1320), true);
            return false;
        }

        //检测
        if (tip) tips.add(get(1300));
        boolean result = true;
        //金钱
        int hasMoney = (int) EcoApi.get(p.getName());
        if (costInfo.getMoney() > 0) {
            boolean checkMoney = hasMoney >= costInfo.getMoney();
            if (tip) tips.add(get(2000, costInfo.getMoney(), get(checkMoney ? 1330 : 1340)));
            if (!checkMoney) {
                if (!force) {
                    //提示结果
                    tips.add(get(2520));
                    ShowApi.tip(p, tips, true);
                    return false;
                }
                else result = false;
            }
        }
        //经验
        int hasExp = p.getTotalExperience();
        if (costInfo.getExp() > 0) {
            boolean checkExp = hasExp >= costInfo.getExp();
            if (tip) tips.add(get(2010, costInfo.getExp(), get(checkExp ? 1330 : 1340)));
            if (!checkExp) {
                if (!force) {
                    //提示结果
                    tips.add(get(2520));
                    ShowApi.tip(p, tips, true);
                    return false;
                }
                else result = false;
            }
        }
        //等级
        int hasLevel = p.getLevel();
        if (costInfo.getLevel() > 0) {
            boolean checkLevel = hasLevel >= costInfo.getLevel();
            if (tip) tips.add(get(2020, costInfo.getLevel(), get(checkLevel ? 1330 : 1340)));
            if (!checkLevel) {
                if (!force) {
                    //提示结果
                    tips.add(get(2520));
                    ShowApi.tip(p, tips, true);
                    return false;
                }
                else result = false;
            }
        }
        //物品
        Inventory inv = p.getInventory();
        if (costInfo.getItems() != null && !costInfo.getItems().isEmpty()) {
            for (ItemInfo itemInfo:costInfo.getItems()) {
                boolean checkItem = false;
                switch (itemInfo.getMode()) {
                    case 1:
                        checkItem = ItemApi.hasExactItem(inv, itemInfo.getIs(), itemInfo.getAmount(), true);
                        break;
                    case 2:
                        checkItem = hasKindAmount(inv, itemInfo.getKind(), itemInfo.getAmount());
                        break;
                }
                if (tip) {
                    String itemName = "";
                    switch (itemInfo.getMode()) {
                        case 1:
                            itemName = NamesApi.getItemName(itemInfo.getIs());
                            break;
                        case 2:
                            if (CostsPlugin.itemKindHook) {
                                KindInfo kindInfo = com.fyxridd.item.kind.api.ItemApi.getKindInfo(itemInfo.getKind());
                                if (kindInfo != null) itemName = kindInfo.getShow();
                                else itemName = itemInfo.getKind();
                            }
                            break;
                    }
                    tips.add(get(2030, itemName, itemInfo.getAmount(), get(checkItem ? 1330 : 1340)));
                }
                if (!checkItem) {
                    if (!force) {
                        //提示结果
                        tips.add(get(2520));
                        ShowApi.tip(p, tips, true);
                        return false;
                    }
                    else result = false;
                }
            }
        }

        //花费
        //金钱
        int costMoney = Math.min(hasMoney, costInfo.getMoney());
        if (costMoney > 0) EcoApi.del(p.getName(), costMoney);
        //经验
        int costExp = Math.min(hasExp, costInfo.getExp());
        if (costExp > 0) p.setTotalExperience(hasExp-costExp);
        //等级
        hasLevel = p.getLevel();
        int costLevel = Math.min(hasLevel, costInfo.getLevel());
        if (costLevel > 0) p.setLevel(hasLevel-costLevel);
        //物品
        if (costInfo.getItems() != null && !costInfo.getItems().isEmpty()) {
            for (ItemInfo itemInfo:costInfo.getItems()) {
                switch (itemInfo.getMode()) {
                    case 1:
                        ItemApi.removeExactItem(inv, itemInfo.getIs(), itemInfo.getAmount(), true, true);
                        break;
                    case 2:
                        removeKindAmount(inv, itemInfo.getKind(), itemInfo.getAmount());
                        break;
                }
            }
        }

        //提示结果
        if (result) tips.add(get(2500));
        else tips.add(get(2510));
        ShowApi.tip(p, tips, true);

        //返回
        return result;
    }

    /**
     * @see com.fyxridd.lib.costs.api.CostsApi#reloadCosts(String)
     */
    public void reloadCosts(String plugin) {
        if (plugin == null) return;
        YamlConfiguration config = UtilApi.loadConfigByUTF8(new File(CoreApi.pluginPath, plugin+File.separator+"costs.yml"));
        if (config == null) return;
        //重置
        costsHash.put(plugin, new HashMap<String, CostInfo>());
        //重新读取
        for (String key:config.getValues(false).keySet()) {
            //money
            int money = config.getInt(key+".money", 0);
            if (money < 0) {
                ConfigApi.log(CostsPlugin.pn, "load costs key '"+key+"' money error");
                money = 0;
            }
            //exp
            int exp = config.getInt(key+".exp", 0);
            if (exp < 0) {
                ConfigApi.log(CostsPlugin.pn, "load costs key '"+key+"' exp error");
                exp = 0;
            }
            //level
            int level = config.getInt(key+".level", 0);
            if (level < 0) {
                ConfigApi.log(CostsPlugin.pn, "load costs key '"+key+"' level error");
                level = 0;
            }
            //items
            List<ItemInfo> items = new ArrayList<>();
            MemorySection ms = (MemorySection)config.get(key+".items");
            if (ms != null) {
                for (String key:ms.getValues(false).keySet()) {
                    MemorySection itemMs = (MemorySection) ms.get(key);
                    ItemInfo itemInfo = null;
                    if (itemMs.contains("exact")) {
                        itemInfo = new ItemInfo(ItemsApi.loadItemStack((MemorySection) itemMs.get("exact")), itemMs.getInt("amount", 1));
                    }else if (itemMs.contains("kind")) {
                        itemInfo = new ItemInfo(itemMs.getString("kind"), itemMs.getInt("amount", 1));
                    }
                    if (itemInfo != null) items.add(itemInfo);
                }
            }
            //添加
            costsHash.get(plugin).put(key, new CostInfo(money, exp, level, items));
        }
    }

    /**
     * 移除指定种类的指定数量物品
     */
    private void removeKindAmount(Inventory inv, String kind, int amount) {
        if (!CostsPlugin.itemKindHook || amount <= 0) return;
        //需要减少的数量
        int need = amount;
        for (int i=0;i<inv.getSize();i++) {
            ItemStack is2 = inv.getItem(i);
            if (is2 != null && is2.getType() != Material.AIR) {
                if (kind.equals(com.fyxridd.item.kind.api.ItemApi.getKind(is2))) {//检测相同成功,减少物品
                    int has = is2.getAmount();
                    if (need <= has) {//结束
                        if (has == need) inv.setItem(i, null);
                        else is2.setAmount(has-need);
                        break;
                    }else {
                        need -= has;
                        inv.setItem(i, null);
                    }
                }
            }
        }
    }

    /**
     * 检测是否有指定数量的指定种类物品
     * @return 如果未挂钩ItemKind插件则返回false
     */
    private boolean hasKindAmount(Inventory inv, String kind, int amount) {
        if (!CostsPlugin.itemKindHook) return false;

        int sum = 0;
        for (int i=0;i<inv.getSize();i++) {
            ItemStack check = inv.getItem(i);
            if (check != null && check.getType() != Material.AIR) {
                if (kind.equals(com.fyxridd.item.kind.api.ItemApi.getKind(check))) {
                    sum += inv.getItem(i).getAmount();
                    if (sum >= amount) return true;
                }
            }
        }
        return false;
    }

    private FancyMessage get(String player, int id, Object... args) {
        return langConfig.getLang().get(player, id, args);
    }
}
