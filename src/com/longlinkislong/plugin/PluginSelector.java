package com.longlinkislong.plugin;

import java.util.List;
import java.util.Map;

/**
 * A helper class that needs to be defined with any group of plugins. The
 * purpose of the PluginSelector is to expose the plugins to the PluginManager
 * which then can instantiate them.
 *
 * @author zmichaels
 * @param <Key> the key for the plugin lookup.
 * @param <Implementation> the base class for the plugins
 * @since 14.12.29
 * @see com.longlinkislong.plugin.PluginManager
 */
public interface PluginSelector<Key, Implementation> {

    /**
     * Retrieves the default plugin instance. This should be a singleton, since
     * it will always return the same object. This only needs to be implemented
     * for plugins that supply features, not plugins that supply instance
     * objects.
     *
     * @return the preferred object.
     * @since 14.12.29
     */
    public Key getPreferred();

    /**
     * Retrieves a list of all the supported plugin keys. It should be possible
     * to then create new objects from any key listed inside this list. It is
     * recommended that the list returned is an unmodifiable list.
     *
     * @return a list of plugin lookups
     * @since 14.12.29
     */
    public List<Key> getSupported();

    /**
     * Populates a map with key, implementation pairs. This method is designed
     * to be called by PluginManager on initialization. Calling it elsewhere is
     * undefined behavior.
     *
     * @param pluginImpl the plugin implementation map.
     * @since 14.12.29
     */
    public void registerImplements(Map<Key, Class<? extends Implementation>> pluginImpl);
}
