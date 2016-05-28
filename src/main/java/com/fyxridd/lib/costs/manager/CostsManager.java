package com.fyxridd.lib.costs.manager;

import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.costs.CostsPlugin;
import com.fyxridd.lib.costs.api.model.CheckCostResult;
import com.fyxridd.lib.costs.api.model.CostResult;
import com.fyxridd.lib.costs.api.model.Coster;
import com.fyxridd.lib.costs.api.model.CosterFactory;
import com.fyxridd.lib.costs.config.LangConfig;
import com.fyxridd.lib.costs.model.CostInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostsManager {
    private LangConfig langConfig;

    //Key 花费工厂
    private Map<String, CosterFactory> costerFactoryMap = new HashMap<>();

    //插件名 类型名 花费信息
    private Map<String, Map<String, CostInfo>> costsHash = new HashMap<>();

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
     * @see com.fyxridd.lib.costs.api.CostsApi#registerCostsFactory(String, CosterFactory)
     */
    public void registerCostsFactory(String key, CosterFactory costerFactory) {
        costerFactoryMap.put(key, costerFactory);
    }

    /**
     * @see com.fyxridd.lib.costs.api.CostsApi#reloadCosts(String)
     */
    public void reloadCosts(String plugin) {
        try {
            if (plugin == null) return;
            YamlConfiguration config = UtilApi.loadConfigByUTF8(new File(CoreApi.pluginPath, plugin+File.separator+"costs.yml"));
            if (config == null) return;
            //重置
            Map<String, CostInfo> map = new HashMap<>();
            costsHash.put(plugin, map);
            //重新读取
            for (String type:config.getValues(false).keySet()) {
                try {
                    List<Coster> costers = new ArrayList<>();
                    map.put(type, new CostInfo(costers));
                    {
                        ConfigurationSection cs = config.getConfigurationSection(type);
                        for (String key:cs.getValues(false).keySet()) {
                            try {
                                CosterFactory costerFactory = costerFactoryMap.get(key);
                                if (costerFactory != null) costers.add(costerFactory.produce(cs.getConfigurationSection(key)));
                                else CoreApi.debug("CosterFactory for '"+key+"' not found!");
                            } catch (Exception e) {
                                throw new Exception("load key '"+key+"' error: "+e.getMessage(), e);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("load type '"+type+"' error: "+e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            //todo
        }
    }

    /**
     * @see com.fyxridd.lib.costs.api.CostsApi#cost(Player, String, String, boolean, boolean)
     */
    public boolean cost(Player p, String plugin, String type, boolean force, boolean tip){
        if (p == null || plugin == null || type == null) return false;

        List<FancyMessage> tips = new ArrayList<>();

        //类型不存在
        Map<String, CostInfo> hash = costsHash.get(plugin);
        if (hash == null) return false;
        CostInfo costInfo = hash.get(type);
        if (costInfo == null) return false;

        //开始检测
        if (tip) tips.add(get(p.getName(), 1300));
        boolean result = true;//花费是否完全成功

        //检测
        for (Coster coster:costInfo.getCosters()) {
            CheckCostResult checkCostResult = coster.checkCost(plugin, p);
            if (!checkCostResult.isSuccess()) result = false;
            if (checkCostResult.getTips() != null) {
                for (String msg:checkCostResult.getTips()) tips.add(MessageApi.convert(msg));
            }
        }

        if (!result && !force) {//花费失败
            tips.add(get(p.getName(), 2520));
        }else {
            //花费
            for (Coster coster:costInfo.getCosters()) {
                CostResult costResult = coster.cost(plugin, p);
                if (result && costResult != CostResult.success) result = false;
            }
            if (result) tips.add(get(p.getName(), 2500));
            else tips.add(get(p.getName(), 2510));
        }

        //提示结果
        MessageApi.send(p, tips, true);

        //返回
        return result;
    }

    public FancyMessage get(String player, int id, Object... args) {
        return langConfig.getLang().get(player, id, args);
    }
}
