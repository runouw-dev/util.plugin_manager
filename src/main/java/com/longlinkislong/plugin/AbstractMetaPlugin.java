/*
 * Copyright (c) 2017, zmichaels
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
            final PluginSelector<String, BaseType> selector = PluginSelector.singletonSelector(plugin.lookup, typedClazz);

            this.registeredPlugins.put(plugin.lookup, plugin);
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
