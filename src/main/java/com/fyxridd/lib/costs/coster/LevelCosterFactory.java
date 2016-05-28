package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.costs.api.model.CosterFactory;
import org.bukkit.configuration.ConfigurationSection;

public class LevelCosterFactory implements CosterFactory<LevelCoster>{
    public static final String KEY = "level";

    @Override
    public LevelCoster produce(ConfigurationSection cs) {
        int value = cs.getInt("value");
        return new LevelCoster(value);
    }
}
