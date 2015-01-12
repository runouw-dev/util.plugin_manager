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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The PluginManager acts as an intermediate between the application and the
 * plugin. Through the PluginManager, the application can instantiate new
 * plugins as defined by the PluginSelector.
 *
 * @author zmichaels
 * @param <Key> Lookup for plugins
 * @param <Implementation> The plugin base type.
 * @since 14.12.29
 * @see com.longlinkislong.plugin.PluginSelector
 */
public final class PluginManager<Key, Implementation> {

    private final Map<Key, Class<? extends Implementation>> implementations = new HashMap<>();
    private PluginSelector<Key, Implementation> selector;
    private final PluginSelectorBuilder<Key, Implementation> builder = new PluginSelectorBuilder();
    private Optional<Implementation> selectedImpl = Optional.empty();

    /**
     * Retrieves a list of plugins supported by the PluginManager
     *
     * @return list of plugins
     * @since 15.01.06
     */
    public List<Key> listPlugins() {
        this.checkSelector();

        return Collections.unmodifiableList(this.selector.getSupported());
    }

    /**
     * Registers another selector for the PluginManager to use.
     *
     * @param selector another selector
     * @since 15.01.12
     */
    public void registerSelector(final PluginSelector<Key, Implementation> selector) {
        this.builder.join(selector);
        this.selector = null;
    }

    /**
     * Registers another selector defined as a PluginSelectorBuilder for the
     * PluginManager to use.
     *
     * @param selectorBuilder another selector
     * @since 15.01.12
     */
    public void registerSelector(final PluginSelectorBuilder<Key, Implementation> selectorBuilder) {
        this.builder.join(selectorBuilder);
        this.selector = null;
    }

    /**
     * Registers another selector defined as a classpath for the PluginManager to use.
     * @param selectorDef the full path to the PluginSelector
     * @throws ClassNotFoundException if the path does not point to a class
     * @since 15.01.12
     */
    public void registerSelector(final String selectorDef) throws ClassNotFoundException {
        this.builder.join(selectorDef);
        this.selector = null;
    }

    private void checkSelector() {
        if (this.selector == null) {
            this.selectedImpl = Optional.empty();
            this.selector = this.builder.getSelector();
            this.implementations.clear();
            this.selector.registerImplements(this.implementations);
        }
    }

    /**
     * Constructs a PluginManager without defining a selector. It is recommended
     * to call registerSelector before calling any other method.
     *
     * @since 15.01.12
     */
    public PluginManager() {

    }

    /**
     * Constructs a new PluginManager with the absolute classname for the
     * PluginSelector. The PluginSelector should already reside within the
     * classpath.
     *
     * @param selectorDef the absolute classname for the PluginSelector
     * including all levels of package nesting.
     * @throws ClassNotFoundException if the PluginSelector was not found.
     * @since 14.12.29
     */
    public PluginManager(final String selectorDef) throws ClassNotFoundException {
        this.registerSelector(selectorDef);
    }

    /**
     * Constructs a new PluginManager from an already defined PluginSelector.
     * The preferred method of retrieving a PluginSelector is through
     * reflection, which guarantees that the system remains loosely linked.
     * Direct instantiation is supplied for code completeness.
     *
     * @param selector the selector to use
     * @since 14.12.29
     */
    public PluginManager(final PluginSelector<Key, Implementation> selector) {
        this.registerSelector(selector);
    }

    /**
     * Constructs a new PluginManager from a PluginSelectorBuilder.
     *
     * @param builder the builder to read values from.
     * @since 15.01.12
     */
    public PluginManager(final PluginSelectorBuilder<Key, Implementation> builder) {
        this.registerSelector(builder);
    }

    /**
     * Retrieves either the implementation of the singleton instance or a new
     * instance of the object in that order.
     *
     * @param key the lookup key to use.
     * @param params optional parameters for calling the implementations
     * @return the implementation, if it exists.
     * @since 14.12.29
     */
    public Optional<? extends Implementation> getImplementation(final Key key, final Object... params) {
        this.checkSelector();

        final Class<? extends Implementation> def = this.implementations.get(key);

        return getImplementation(def, params);
    }

    /**
     * Reselects the preferred implementation. If the preferred implementation
     * was initialized through a constructor, a new instance of it will be
     * created.
     *
     * @return the preferred implementation after being reselected.
     * @since 14.12.29
     */
    public Implementation selectPreferred() {
        this.checkSelector();

        final Implementation impl = this.getImplementation(
                this.selector.getPreferred())
                .get();

        this.selectedImpl = Optional.of(impl);

        return impl;
    }

    /**
     * Retrieves the preferred implementation. This is defined as the default
     * form of the plugin. This method will always return the same instance of
     * the preferred implementation unless the object was initialized through a
     * constructor and the retained value was reset by calling selectPreferred.
     * Because of that, this method is best suited towards singleton objects.
     *
     * @return the preferred implementation.
     * @since 14.12.29
     */
    public Implementation getPreferred() {
        this.checkSelector();

        return (this.selectedImpl.orElseGet(this::selectPreferred));
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else {
            if (other instanceof PluginManager) {
                final PluginManager o = (PluginManager) other;

                this.checkSelector();
                o.checkSelector();

                return (o.implementations.equals(this.implementations));
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        this.checkSelector();

        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.implementations);
        return hash;
    }

    @Override
    public String toString() {
        this.checkSelector();

        return String.format("PluginManager supported plugins: %s", this.listPlugins());
    }

    private static <Type> Type getImplementationFromSingleton(final Class<Type> def, final Object... params) {
        try {
            final Method singletonGetter = def.getMethod("getInstance");
            final Type impl = (Type) singletonGetter.invoke(null, params);

            return impl;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return null;
        }
    }

    private static <Type> Type getImplementationFromField(final Class<Type> def) {
        try {
            final Field singletonInstance = def.getField("INSTANCE");

            if (def.isAssignableFrom(singletonInstance.getType())) {
                return (Type) singletonInstance.get(null);
            }

            return null;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            return null;
        }
    }

    private static <Type> Type getImplementationFromNewInstance(final Class<Type> def, final Object... params) {
        try {
            for (final Constructor c : def.getConstructors()) {
                if (c.getParameterCount() == params.length) {
                    return (Type) c.newInstance(params);
                }
            }
            return null;
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * This method attempts to retrieve an instance of an object by trying
     * multiple common design patterns. If the object exists as a singleton, it
     * will attempt to retrieve the instance by first calling getInstance and
     * then trying to retrieve the value of INSTANCE. If neither of those exist
     * or execute without error, it will attempt to create a new instance by
     * calling a 0-parameter constructor. If that fails, it will then
     * brute-force initialize the object. In the instance that an implementation
     * is never obtained, an empty Optional will be returned.
     *
     * @param <Type> the type of the object.
     * @param def the class definition of the object
     * @param params
     * @return An Optional that may contain an instance of the object.
     * @since 14.12.29
     */
    public static <Type> Optional<Type> getImplementation(final Class<Type> def, final Object... params) {
        Objects.requireNonNull(def, "Class definition cannot be null!");

        Type impl = getImplementationFromSingleton(def, params);

        if (impl == null) {
            impl = getImplementationFromField(def);
        }

        if (impl == null) {
            impl = getImplementationFromNewInstance(def, params);
        }

        return Optional.ofNullable(impl);
    }
}
