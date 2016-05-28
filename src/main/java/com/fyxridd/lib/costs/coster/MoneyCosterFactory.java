package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.costs.api.model.CosterFactory;
import org.bukkit.configuration.ConfigurationSection;

public class MoneyCosterFactory implements CosterFactory<MoneyCoster>{
    public static final String KEY = "money";

    @Override
    public MoneyCoster produce(ConfigurationSection cs) {
        int value = cs.getInt("value");
        return new MoneyCoster(value);
    }
}
