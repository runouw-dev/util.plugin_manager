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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PluginManager<Key, Implementation> {

    private final Map<Key, Class<? extends Implementation>> implementations = new HashMap<>();
    private final Map<Class<? extends Implementation>, MethodHandle> constructors = new HashMap<>();

    private PluginSelector<Key, Implementation> selector;
    private final PluginSelectorBuilder<Key, Implementation> builder = new PluginSelectorBuilder<>();
    private Implementation selectedImpl = null;
    private final Map<Key, List<WeakReference<HotSwapPlugin<Implementation>>>> hotswapPlugins = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

    /**
     * Removes a plugin from listing. This will not unload a class. To unload a
     * class, the classloader used must be garbage collected.
     *
     * @param key the key to remove.
     * @since 15.12.10
     */
    public void removePlugin(final Key key) {
        this.implementations.remove(key);
        this.hotswapPlugins.remove(key);
    }

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
    public final void registerSelector(final PluginSelector<Key, Implementation> selector) {
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
    public final void registerSelector(final PluginSelectorBuilder<Key, Implementation> selectorBuilder) {
        this.builder.join(selectorBuilder);
        this.selector = null;
    }

    /**
     * Registers another selector defined as a classpath for the PluginManager
     * to use.
     *
     * @param selectorDef the full path to the PluginSelector
     * @throws ClassNotFoundException if the path does not point to a class
     * @since 15.01.12
     */
    public final void registerSelector(final String selectorDef) throws ClassNotFoundException {
        this.builder.join(selectorDef);
        this.selector = null;
    }

    /**
     * Forces the internal plugin selector to be rebuilt.
     *
     * @since 15.07.28
     */
    public void rebuildSelector() {
        this.selector = null;
        this.checkSelector();
    }

    private void checkSelector() {
        if (this.selector == null) {
            this.selectedImpl = null;
            this.selector = this.builder.getSelector();
            this.implementations.clear();
            this.selector.registerImplements(this.implementations);

            for (Key key : this.hotswapPlugins.keySet()) {
                List<WeakReference<HotSwapPlugin<Implementation>>> plugins = this.hotswapPlugins.get(key);

                if (!this.implementations.containsKey(key)) {
                    continue;
                }

                final String keyType = this.implementations.get(key).getTypeName();

                // remove all dead plugins
                plugins = plugins.stream()
                        .map(WeakReference::get)
                        .filter(Objects::nonNull)
                        .map(p -> {
                            if (!p.getPluginType().equals(keyType)) {
                                p.swapPlugin(this.getImplementation(key));
                            }

                            return p;
                        })
                        .map(WeakReference::new)
                        .collect(Collectors.toList());

                this.hotswapPlugins.put(key, plugins);
            }
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
    public Implementation getImplementation(final Key key, final Object... params) {
        this.checkSelector();

        if (!this.implementations.containsKey(key)) {
            throw new PluginException("Could not find plugin: " + key);
        }

        final Class<? extends Implementation> def = this.implementations.get(key);
        MethodHandle handle = this.constructors.get(def);

        if (handle == null) {
            handle = getSingletonGetter(def, params)
                    .orElseGet(() -> getFieldGetter(def)
                            .orElseGet(() -> getConstructorGetter(def, params)
                                    .get()));

            this.constructors.put(def, handle);
        }

        try {
            if (params.length == 0) {
                return (Implementation) handle.invoke();
            } else {
                return (Implementation) handle.invokeWithArguments(params);
            }
        } catch (Throwable ex) {
            LOGGER.error("Unable to get implementation!");
            LOGGER.debug(ex.getMessage(), ex);            
            return null;
        }
    }

    /**
     * Retrieves a plugin implementation that will self-update with
     * replacements.
     *
     * @param key the key for the plugin.
     * @return the plugin.
     * @since 15.07.28
     */
    public HotSwapPlugin<Implementation> getHotSwapImplementation(final Key key) {
        final HotSwapPlugin<Implementation> plugin = new HotSwapPlugin(this.getImplementation(key));
        final List<WeakReference<HotSwapPlugin<Implementation>>> plugins = this.hotswapPlugins.getOrDefault(key, new ArrayList<>());

        plugins.add(new WeakReference<>(plugin));

        this.hotswapPlugins.put(key, plugins);

        return plugin;
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
                this.selector.getPreferred());

        this.selectedImpl = impl;

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

        return (this.selectedImpl == null) ? this.selectPreferred() : this.selectedImpl;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof PluginManager) {
            final PluginManager<?, ?> o = (PluginManager) other;

            this.checkSelector();
            o.checkSelector();

            return (o.implementations.equals(this.implementations));
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

    private static <Type> Optional<MethodHandle> getSingletonGetter(final Class<Type> def, final Object... params) {
        try {
            if (params.length == 0) {
                final MethodType mt = MethodType.methodType(def);
                final MethodHandle getter = MethodHandles.lookup().findStatic(def, "getInstance", mt);

                return Optional.of(getter);
            } else {
                return Optional.empty();
            }
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException ex) {
            LOGGER.trace(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private static <Type> Optional<MethodHandle> getFieldGetter(final Class<Type> def) {
        try {
            final MethodHandle getter = MethodHandles.lookup().findStaticGetter(def, "INSTANCE", def);

            return Optional.of(getter);
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            LOGGER.trace(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private static <Type> Optional<MethodHandle> getConstructorGetter(final Class<Type> def, final Object... params) {
        try {
            if (params.length == 0) {
                final MethodType mt = MethodType.methodType(void.class);
                final MethodHandle handle = MethodHandles.lookup().findConstructor(def, mt);

                return Optional.of(handle);
            } else {
                final int pLen = params.length;
                final Class<?>[] cParams = new Class[pLen];

                for (int i = 0; i < pLen; i++) {
                    cParams[i] = params[i].getClass();
                }

                final Constructor[] ctrs = def.getConstructors();

                for (Constructor ctr : ctrs) {
                    final Class<?>[] testParams = ctr.getParameterTypes();

                    if (testParams.length == pLen) {
                        boolean isCorrectParams = true;

                        for (int i = 0; i < pLen; i++) {
                            if (!testParams[i].isAssignableFrom(cParams[i])) {
                                isCorrectParams = false;
                                break;
                            }
                        }

                        if (isCorrectParams) {
                            final MethodType mt = MethodType.methodType(void.class, testParams);
                            final MethodHandle handle = MethodHandles.lookup().findConstructor(def, mt);

                            return Optional.of(handle);
                        }
                    }
                }

                LOGGER.error("Unable to scan constructors for matching definition!");
                return Optional.empty();
            }
        } catch (IllegalAccessException | NoSuchMethodException ex) {
            LOGGER.trace(ex.getMessage(), ex);            
            return Optional.empty();
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
     * is never obtained, an exception will be thrown.
     *
     * @param <Type> the type of the object.
     * @param def the class definition of the object
     * @param params list of parameters
     * @return an instance of the object
     * @since 14.12.29
     */
    public static <Type> Type getImplementation(final Class<Type> def, final Object... params) {
        Objects.requireNonNull(def, "Class definition cannot be null!");

        final Optional<MethodHandle> singleton = getSingletonGetter(def, params);

        try {
            if (singleton.isPresent()) {
                if (params.length == 0) {
                    return (Type) singleton.get().invoke();
                } else {
                    return (Type) singleton.get().invoke(params);
                }
            } else {
                final Optional<MethodHandle> field = getFieldGetter(def);

                if (field.isPresent()) {
                    return (Type) field.get().invokeExact();
                } else {
                    final Optional<MethodHandle> constructor = getConstructorGetter(def, params);

                    if (constructor.isPresent()) {
                        if (params.length == 0) {
                            return (Type) constructor.get().invoke();
                        } else {
                            return (Type) constructor.get().invoke(params);
                        }
                    } else {
                        LOGGER.error("Unable to find any type of constructor!");
                        return null;
                    }
                }
            }
        } catch (Throwable ex) {                        
            LOGGER.error("Unable to get implementation!");
            LOGGER.debug(ex.getMessage(), ex);            

            return null;
        }
    }
}
