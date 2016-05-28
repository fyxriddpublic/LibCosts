package com.fyxridd.lib.costs;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.costs.api.CostsApi;
import com.fyxridd.lib.costs.config.LangConfig;
import com.fyxridd.lib.costs.coster.ExpCosterFactory;
import com.fyxridd.lib.costs.coster.ItemCosterFactory;
import com.fyxridd.lib.costs.coster.LevelCosterFactory;
import com.fyxridd.lib.costs.coster.MoneyCosterFactory;
import com.fyxridd.lib.costs.manager.CostsManager;

public class CostsPlugin extends SimplePlugin{
    public static CostsPlugin instance;

    private CostsManager costsManager;

    @Override
    public void onEnable() {
        instance = this;

        //注册配置
        ConfigApi.register(pn, LangConfig.class);

        costsManager = new CostsManager();

        //注册花费器
        CostsApi.registerCostsFactory(MoneyCosterFactory.KEY, new MoneyCosterFactory());
        CostsApi.registerCostsFactory(ExpCosterFactory.KEY, new ExpCosterFactory());
        CostsApi.registerCostsFactory(LevelCosterFactory.KEY, new LevelCosterFactory());
        CostsApi.registerCostsFactory(ItemCosterFactory.KEY, new ItemCosterFactory());

        super.onEnable();
    }

    public CostsManager getCostsManager() {
        return costsManager;
    }
}