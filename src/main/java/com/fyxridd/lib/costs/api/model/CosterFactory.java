package com.fyxridd.lib.costs.api.model;

import org.bukkit.configuration.ConfigurationSection;

/**
 * 花费器工厂
 */
public interface CosterFactory<T extends Coster> {
    /**
     * 生产花费器
     */
    T produce(ConfigurationSection cs);
}
