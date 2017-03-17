/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simplified implementation of MetaPlugin that can be used by most
 * implementations.
 *
 * @author zmichaels
 * @param <BaseType> the base type
 */
public abstract class AbstractMetaPlugin<BaseType> implements MetaPlugin {

    private final Map<String, MetaClass> registeredPlugins = new HashMap<>();

    /**
     * Retrieves the PluginManager used. The simplest implementation of
     * AbstractMetaPlugin will use one static PluginManager shared across all
     * instances. This is not required. All that is required is that
     * getPluginManager is consistent.
     *
     * @return the PluginManager
     */
    protected abstract PluginManager<String, BaseType> getPluginManager();

    @Override
    public boolean register(final MetaClass plugin) {
        if (supportsType(plugin.clazz)) {
            final Class<? extends BaseType> typedClazz = (Class<? extends BaseType>) plugin.clazz;
            final PluginSelector<String, BaseType> selector = PluginSelector.singletonSelector(plugin.id, typedClazz);

            this.registeredPlugins.put(plugin.id, plugin);
            this.getPluginManager().registerSelector(selector);

            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public <T> Optional<T> newInstance(final String id) {
        return Optional.ofNullable((T) this.getPluginManager().getImplementation(id));
    }

    /**
     * Retrieves the description of the plugin (if it was registered)
     * @param id the id of the plugin
     * @return the description.
     */
    public Optional<String> getDescription(final String id) {
        return Optional.ofNullable(this.registeredPlugins.get(id))
                .map(meta -> meta.description);
    }

    /**
     * Retrieves the name of the plugin (if it was registered)
     * @param id the id of the plugin
     * @return the name
     */
    public Optional<String> getName(final String id) {
        return Optional.ofNullable(this.registeredPlugins.get(id))
                .map(meta -> meta.name);
    }

    /**
     * Retrieves the class definition of the plugin (if it was registered)
     * @param id the id of the plugin
     * @return the class definition
     */
    public Optional<Class<? extends BaseType>> getClass(final String id) {
        return Optional.ofNullable(this.registeredPlugins.get(id))
                .map(meta -> meta.clazz)
                .map(clazz -> (Class<? extends BaseType>) clazz);
    }
}
