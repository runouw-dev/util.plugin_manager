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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The PluginSelectorBuilder is a helper class that handles creating a generic
 * PluginSelector.
 *
 * @author zmichaels
 * @param <KeyT> the KeyType to use
 * @param <ClassT> the base type to use for the Plugins
 * @since 15.01.12
 */
public class PluginSelectorBuilder<KeyT, ClassT> {

    private final List<KeyT> keys = new ArrayList<>();
    private final Map<KeyT, Class<? extends ClassT>> pluginMap = new HashMap<>();
    private Optional<Class<? extends ClassT>> preferred = Optional.empty();

    /**
     * Registers a key, plugin pair.
     *
     * @param key the key to register
     * @param plugin the plugin to register
     * @return self-reference.
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> register(
            final KeyT key,
            final Class<? extends ClassT> plugin) {

        if (!this.pluginMap.containsKey(key)) {
            this.keys.add(key);
        }
        this.pluginMap.put(key, plugin);

        return this;
    }

    /**
     * Registers a plugin as the preferred plugin. Supplying null will set the
     * preferred plugin to the first plugin registered.
     *
     * @param preferredPlugin the preferred plugin or null.
     * @return self-reference
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> setPreferred(
            final Class<? extends ClassT> preferredPlugin) {

        this.preferred = Optional.ofNullable(preferredPlugin);

        return this;
    }

    /**
     * Returns the preferred plugin if set. If it has not been set, it should
     * return the first plugin registered.
     *
     * @return the preferred plugin.
     * @since 15.01.12
     */
    public Class<? extends ClassT> getPreferred() {
        return this.preferred.orElse(this.pluginMap.get(this.keys.get(0)));
    }

    /**
     * Joins all of the Plugins hosted by another PluginSelector with the
     * PluginSelector held by this PluginSelectorBuilder. The key list is
     * guaranteed to contain no copies and any duplicate key,plugin pairs will
     * be overwritten by the key,plugin pair from the otherSelector.
     *
     * @param otherSelector the PluginSelector to merge with
     * @return self-reference
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> join(
            final PluginSelector<KeyT, ClassT> otherSelector) {

        otherSelector.getSupported().stream()
                .filter((key) -> (!pluginMap.containsKey(key)))
                .forEach(this.keys::add);

        otherSelector.registerImplements(pluginMap);

        return this;
    }

    /**
     * Joins all of the Plugins hosted by this PluginSelectorBuilder with
     * another PluginSelectorBuilder. The key list is guaranteed to contain no
     * copies and any duplicate key,plugin pairs will be overwritten by the
     * definition in otherBuilder.
     *
     * @param otherBuilder the PluginSelectorBuilder to merge with
     * @return self-reference
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> join(
            final PluginSelectorBuilder<KeyT, ClassT> otherBuilder) {

        return this.join(otherBuilder.getSelector());                
    }

    /**
     * Joins all of the Plugins hosted by this PluginSelectorBuilder with those
     * defined by the canonical path for a PluginSelector. The key list is
     * guaranteed to contain no copies and any duplicate key,plugin pairs will
     * be overwritten by the definition in otherBuilder.
     *
     * @param pluginDef the string path for a PluginSelector
     * @param params Optional parameters to use in initializing the
     * PluginSelector.
     * @return self-reference
     * @throws NullPointerException if no valid instance of the PluginSelector
     * could be initialized.
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> join(
            final Class<? extends PluginSelector<KeyT, ClassT>> pluginDef,
            final Object... params)
            throws NullPointerException {

        return this.join(PluginManager.getImplementation(pluginDef, params)
                .orElseThrow(NullPointerException::new));
    }

    /**
     * Joins all of the Plugins hosted by this PluginSelectorBuilder with those
     * defined by the canonical path for a PluginSelector. The key list is
     * guaranteed to contain no copies and any duplicate key,plugin pairs will
     * be overwritten by the definition in otherBuilder.
     *
     * @param otherLoc the string path for another PluginSelector.
     * @param params Optional parameters to use in initializing the
     * PluginSelector.
     * @return self-reference
     * @throws ClassNotFoundException if the path is invalid
     * @throws NullPointerException if no valid instance of the other
     * PluginSelector could be initialized.
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> join(
            final String otherLoc, final Object... params)
            throws ClassNotFoundException, NullPointerException {

        final Class def = Class.forName(otherLoc);

        return this.join((Class<PluginSelector<KeyT, ClassT>>) def);
    }

    /**
     * Removes a key, plugin pair from the PluginSelectorBuilder.
     *
     * @param key the key to remove
     * @return self-reference.
     * @since 15.01.12
     */
    public PluginSelectorBuilder<KeyT, ClassT> remove(final KeyT key) {
        if (this.pluginMap.containsKey(key)) {
            this.keys.remove(key);
            this.pluginMap.remove(key);
        }

        return this;
    }

    /**
     * Creates a new PluginSelector that reflects the plugins registered.
     *
     * @return the PluginSelector
     * @since 15.01.12
     */
    public PluginSelector<KeyT, ClassT> getSelector() {
        final List<KeyT> readOnlyNames = Collections.unmodifiableList(keys);

        return new PluginSelector<KeyT, ClassT>() {

            @Override
            public List<KeyT> getSupported() {
                return readOnlyNames;
            }

            @Override
            public void registerImplements(Map<KeyT, Class<? extends ClassT>> pluginImpl) {
                pluginImpl.putAll(pluginMap);
            }
        };
    }

    @Override
    public String toString() {
        return String.format("PluginSelectorBuilder plugins: %s", this.keys);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof PluginSelectorBuilder) {
            final PluginSelectorBuilder o = (PluginSelectorBuilder) other;

            return (o.keys.equals(this.keys)
                    && (o.pluginMap.equals(this.pluginMap))
                    && (o.preferred.equals(this.preferred)));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.keys);
        hash = 83 * hash + Objects.hashCode(this.pluginMap);
        hash = 83 * hash + Objects.hashCode(this.preferred);
        return hash;
    }
}
