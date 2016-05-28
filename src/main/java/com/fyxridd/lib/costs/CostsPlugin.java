package com.fyxridd.lib.costs;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.costs.config.LangConfig;
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

        super.onEnable();
    }

    public CostsManager getCostsManager() {
        return costsManager;
    }
}