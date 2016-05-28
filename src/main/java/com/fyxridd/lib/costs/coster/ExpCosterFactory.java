package com.fyxridd.lib.costs.coster;

import com.fyxridd.lib.costs.api.model.CosterFactory;
import org.bukkit.configuration.ConfigurationSection;

public class ExpCosterFactory implements CosterFactory<ExpCoster>{
    public static final String KEY = "exp";

    @Override
    public ExpCoster produce(ConfigurationSection cs) {
        int value = cs.getInt("value");
        return new ExpCoster(value);
    }
}
