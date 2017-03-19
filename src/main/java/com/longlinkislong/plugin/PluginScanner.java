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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * PluginScanner is a utility class designed for delegating other classes
 * (loaded via SPI) to handling Plugins.
 *
 * @author zmichaels
 */
public final class PluginScanner {

    private final List<PluginHandler> handlers = new ArrayList<>();
    private final Set<PluginHandler> uniquePlugins = new HashSet<>();

    /**
     * Constructs a new PluginScanner. This will automatically load all
 PluginHandler instances registered via SPI.
     */
    public PluginScanner() {
        final ServiceLoader<PluginHandler> spiPlugins = ServiceLoader.load(PluginHandler.class);

        for (PluginHandler plugin : spiPlugins) {
            addPluginHandler(plugin);
        }
    }

    /**
     * Adds a PluginHandler
     *
     * @param plugin the PluginHandler to add
     * @return true if any structure changes occurred.
     */
    public boolean addPluginHandler(final PluginHandler plugin) {
        if (uniquePlugins.add(plugin)) {
            handlers.add(plugin);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a PluginHandler
     *
     * @param plugin the PluginHandler to remove
     * @return true if any structure changes occurred.
     */
    public boolean removeMetaPlugin(final PluginHandler plugin) {
        if (uniquePlugins.remove(plugin)) {
            handlers.remove(plugin);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves an unmodifiable copy of the list of MetaPlugins
     *
     * @return the MetaPlugins
     */
    public List<PluginHandler> getPlugins() {
        return Collections.unmodifiableList(handlers);
    }

    /**
     * Retrieves a stream of registered MetaPlugins
     *
     * @return the Stream of MetaPlugins
     */
    public Stream<PluginHandler> plugins() {
        return handlers.stream();
    }

    /**
     * Retrieves the PluginHandler that registered the class definition
     *
     * @param <T> the class type
     * @param supportType the class definition
     * @return the PluginHandler if it exists
     */
    public <T> Optional<PluginHandler> getPluginHandler(final Class<T> supportType) {
        return plugins()
                .filter(m -> m.supportsType(supportType))
                .findFirst();
    }

    /**
     * Attempts to create a new instance of the given plugin
     *
     * @param <T> the base type
     * @param baseType the base class definition
     * @param id the id
     * @return the instance if it exists
     */
    public <T> Optional<T> newInstance(final Class<T> baseType, final String id) {
        return getPluginHandler(baseType)
                .flatMap(m -> m.newInstance(id));
    }

    /**
     * Scans an array of Classes for plugins. This is the same as calling
     * [code]scan(plugins, 0)[/code]
     *
     * @param plugins the array of plugins.
     */
    public void scan(final Class<?>... plugins) {
        scan(plugins, 0);
    }

    /**
     * Scans an array of Classes with offset for plugins. This is the same as
     * calling [code]scan(plugins, offset, plugins.length - offset)[/code]
     *
     * @param plugins the array of plugins.
     * @param offset the offset to begin scanning the plugins.
     */
    public void scan(final Class<?>[] plugins, final int offset) {
        scan(plugins, offset, plugins.length - offset);
    }

    /**
     * Scans a segment of an array of Classes for plugins. This is the same as
     * calling [code]scan(Arrays.stream(plugins, offset, offset + length)[/code]
     *
     * @param plugins the array of plugins.
     * @param offset the offset to begin scanning the plugins.
     * @param length the number of plugins to scan.
     */
    public void scan(final Class<?>[] plugins, final int offset, final int length) {
        scan(Arrays.stream(plugins, offset, offset + length));
    }

    /**
     * Scans for plugins by iterating over a Splitterator. This is the same as
     * calling [code]scan(StreamSupport.stream(splitPlugins, false))[/code]
     *
     * @param splitPlugins the plugins to scan.
     */
    public void scan(final Spliterator<Class<?>> splitPlugins) {
        scan(StreamSupport.stream(splitPlugins, false));
    }

    /**
     * Scans for plugins by iterating over an Iterator. This is the same as
     * calling [code]scan(() -> itPlugins)[/code]
     *
     * @param itPlugins the plugins to scan.
     */
    public void scan(final Iterator<Class<?>> itPlugins) {
        // translates the Iterator to an Iterable
        scan(() -> itPlugins);
    }

    /**
     * Scans for plugins by iterating over an Iterable. This is the same as
     * calling [code]scan(itPlugins.spliterator())[/code].
     *
     * @param itPlugins the plugins to scan.
     */
    public void scan(final Iterable<Class<?>> itPlugins) {
        scan(itPlugins.spliterator());
    }

    /**
     * Processes a Stream of classes to be used as plugins. PluginHandler
     * handlers are loaded via SPI. A PluginHandler is required for registering
     * any plugins. If a plugin is registered by a PluginHandler, any accessible
     * static final methods with the annotation Plugin.OnLoad will execute.
     *
     * @param pluginStream the stream to handle.
     */
    public void scan(final Stream<Class<?>> pluginStream) {
        List<Class<?>> clz = pluginStream.collect(Collectors.toList());
        
        clz.stream()
                .flatMap(handler -> Arrays.stream(handler.getMethods()))
                .filter(mth -> mth.isAnnotationPresent(Plugin.GetHandler.class))
                .forEach(this::invokeGetHandler);
        
        clz.stream()
                .filter(cl -> cl.isAnnotationPresent(Plugin.class))
                .map(PluginScanner::descriptorFromClass)
                .forEach(this::process);
        
        clz.stream()
                .flatMap(handler -> Arrays.stream(handler.getMethods()))
                .filter(mth -> mth.isAnnotationPresent(Plugin.OnLoad.class))
                .forEach(PluginScanner::invokeMethodTestStatic);
        
        /*
        pluginStream
                .filter(plugin -> plugin.isAnnotationPresent(Plugin.class))
                .map(PluginDescriptor::new)
                .map(descriptor -> Arrays.stream(descriptor.clazz.getFields())
                .filter(PluginScanner::isStaticFinal)
                .filter(field -> field.isAnnotationPresent(Plugin.Lookup.class))
                .map(PluginScanner::getField)
                .findFirst()
                .map(descriptor::withLookup)
                .orElse(descriptor))
                .map(descriptor -> Arrays.stream(descriptor.clazz.getFields())
                .filter(PluginScanner::isStaticFinal)
                .filter(field -> field.isAnnotationPresent(Plugin.Description.class))
                .map(PluginScanner::getField)
                .findFirst()
                .map(descriptor::withDescription)
                .orElse(descriptor))
                .map(descriptor -> Arrays.stream(descriptor.clazz.getFields())
                .filter(PluginScanner::isStaticFinal)
                .filter(field -> field.isAnnotationPresent(Plugin.Name.class))
                .map(PluginScanner::getField)
                .findFirst()
                .map(descriptor::withDescription)
                .orElse(descriptor))
                .filter(handler -> process(handlers, handler))
                .map(handler -> handler.clazz)
                .flatMap(handler -> Arrays.stream(handler.getMethods()))
                .filter(PluginScanner::isStaticFinal)
                .forEach(PluginScanner::invokeMethod);
        */
    }
    
    private static PluginDescriptor descriptorFromClass(Class<?> clazz){
        PluginDescriptor descriptor = new PluginDescriptor(clazz);
        
        descriptor = Arrays.stream(clazz.getFields())
                .filter(field -> field.isAnnotationPresent(Plugin.Lookup.class))
                .map(PluginScanner::getFieldTestStaticFinal)
                .findFirst().map(descriptor::withLookup).orElse(descriptor);
        
        descriptor = Arrays.stream(clazz.getFields())
                .filter(field -> field.isAnnotationPresent(Plugin.Name.class))
                .map(PluginScanner::getFieldTestStaticFinal)
                .findFirst().map(descriptor::withName).orElse(descriptor);
        
        descriptor = Arrays.stream(clazz.getFields())
                .filter(field -> field.isAnnotationPresent(Plugin.Description.class))
                .map(PluginScanner::getFieldTestStaticFinal)
                .findFirst().map(descriptor::withDescription).orElse(descriptor);
        
        return descriptor;
    }

    /*
    private static boolean isStaticFinal(final Field field) {
        final int mods = field.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;
        final boolean isFinal = (mods & Modifier.FINAL) != 0;
        
        return isStatic && isFinal;
    }
    
    private static boolean isStaticField(final Field field) {
        final int mods = field.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;
        
        return isStatic;
    }

    private static boolean isStaticFinal(final Method method) {
        final int mods = method.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;
        final boolean isFinal = (mods & Modifier.FINAL) != 0;

        return isStatic && isFinal;
    }
    
    private static boolean isStaticMethod(final Method method) {
        final int mods = method.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;

        return isStatic;
    }
    */
    private static void invokeMethodTestStatic(final Method method) {
        int mod = method.getModifiers();
        if(!Modifier.isStatic(mod)){
            throw new RuntimeException("Method: " + method.getName() + " is not static");
        }
        
        try {
            method.setAccessible(true);
            
            method.invoke(null);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to invoke method: " + method.getName(), ex);
        }
    }
    
    private static void invokeOnLoadMethods(Class<?> clazz){
        Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Plugin.OnLoad.class))
                .forEach(PluginScanner::invokeMethodTestStatic);
    }
    
    private void invokeGetHandler(Method method){
        int mod = method.getModifiers();
        if(!Modifier.isStatic(mod)){
            throw new RuntimeException("Method: " + method.getName() + " is not static");
        }
        
        try {
            method.setAccessible(true);
            
            PluginHandler hnd = (PluginHandler) method.invoke(null);
            
            addPluginHandler(hnd);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Unable to invoke GetHandler method: " + method.getName(), ex);
        }
    }
    
    /*
    private static String getField(final Field field) {
        try {
            field.setAccessible(true);
            
            return field.get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to access field: " + field.getName(), ex);
        }
    }
    */
    
    private static String getFieldTestStaticFinal(final Field field) {
        int mod = field.getModifiers();
        if(!Modifier.isFinal(mod)){
            throw new RuntimeException("Field: " + field.getName() + " is not final");
        }
        if(!Modifier.isStatic(mod)){
            throw new RuntimeException("Field: " + field.getName() + " is not static");
        }
        
        try {
            field.setAccessible(true);
            
            return field.get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to access field: " + field.getName(), ex);
        }
    }

    private boolean process(final PluginDescriptor plugin) {
        return this.handlers.stream()
                .filter(h -> h.register(plugin))
                .findFirst()
                .isPresent();
    }
}
