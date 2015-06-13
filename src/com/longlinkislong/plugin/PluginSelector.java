/*
 * Copyright (c) 2015, zmichaels
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.longlinkislong.plugin;

import java.util.ArrayList;
import java.util.Collections;
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
    default public Key getPreferred() {
        return this.getSupported().get(0);
    }

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

    /**
     * Creates a new PluginSelector for a single key, implementation pair.
     *
     * @param <Key> the type of key
     * @param <Implementation> the type of implementation
     * @param key the key
     * @param impl the class object for the implementation
     * @return the PluginSelector
     * @since 15.06.12
     */
    public static <Key, Implementation> PluginSelector<Key, Implementation> singletonSelector(
            final Key key, final Class<? extends Implementation> impl) {

        return new PluginSelector<Key, Implementation>() {

            @Override
            public List<Key> getSupported() {
                return Collections.singletonList(key);
            }

            @Override
            public void registerImplements(Map<Key, Class<? extends Implementation>> pluginImpl) {
                pluginImpl.put(key, impl);
            }

        };
    }

    /**
     * Constructs a new PluginSelector from two PluginSelectors.
     *
     * @param <Key> the key object
     * @param <Implementation> the implementation
     * @param first the first plugin selector
     * @param second the second plugin selector
     * @return the combined selectors
     * @since 15.06.12
     */
    public static <Key, Implementation> PluginSelector<Key, Implementation> join(
            final PluginSelector<Key, Implementation> first,
            final PluginSelector<Key, Implementation> second) {

        final List<Key> allKeys = new ArrayList<>();

        allKeys.addAll(first.getSupported());
        allKeys.addAll(second.getSupported());

        return new PluginSelector<Key, Implementation>() {

            @Override
            public List<Key> getSupported() {

                return Collections.unmodifiableList(allKeys);
            }

            @Override
            public void registerImplements(Map<Key, Class<? extends Implementation>> pluginImpl) {
                first.registerImplements(pluginImpl);
                second.registerImplements(pluginImpl);
            }

        };
    }       
}
