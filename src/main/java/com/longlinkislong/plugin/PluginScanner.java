/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * PluginScanner is a utility class designed for delegating other classes
 * (loaded via SPI) to handling Plugins.
 *
 * @author zmichaels
 */
public final class PluginScanner {

    private final List<MetaPlugin> metaPlugins = new ArrayList<>();
    private final Set<MetaPlugin> uniquePlugins = new HashSet<>();

    public PluginScanner() {
        final ServiceLoader<MetaPlugin> spiPlugins = ServiceLoader.load(MetaPlugin.class);

        for (MetaPlugin plugin : spiPlugins) {
            addMetaPlugin(plugin);
        }
    }

    /**
     * Adds a MetaPlugin
     * @param plugin the MetaPlugin to add
     * @return true if any structure changes occurred.
     */
    public boolean addMetaPlugin(final MetaPlugin plugin) {
        if (uniquePlugins.add(plugin)) {
            metaPlugins.add(plugin);
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Removes a MetaPlugin
     * @param plugin the MetaPlugin to remove
     * @return true if any structure changes occurred.
     */
    public boolean removeMetaPlugin(final MetaPlugin plugin) {
        if (uniquePlugins.remove(plugin)) {
            metaPlugins.remove(plugin);
            
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
    public List<MetaPlugin> getPlugins() {
        return Collections.unmodifiableList(metaPlugins);
    }

    /**
     * Retrieves a stream of registered MetaPlugins
     *
     * @return the Stream of MetaPlugins
     */
    public Stream<MetaPlugin> plugins() {
        return metaPlugins.stream();
    }

    /**
     * Retrieves the MetaPlugin that registered the class definition
     *
     * @param <T> the class type
     * @param supportType the class definition
     * @return the MetaPlugin if it exists
     */
    public <T> Optional<MetaPlugin> getMetaPlugin(final Class<T> supportType) {
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
        return getMetaPlugin(baseType)
                .flatMap(m -> m.newInstance(id));
    }

    public void scan(final Class<?>[] plugins) {
        scan(plugins, 0);
    }

    public void scan(final Class<?>[] plugins, final int offset) {
        scan(plugins, offset, plugins.length - offset);
    }

    public void scan(final Class<?>[] plugins, final int offset, final int length) {
        scan(Arrays.stream(plugins, offset, offset + length));
    }

    public void scan(final Spliterator<Class<?>> splitPlugins) {
        scan(StreamSupport.stream(splitPlugins, false));
    }

    public void scan(final Iterable<Class<?>> itPlugins) {
        scan(itPlugins.spliterator());
    }

    /**
     * Processes a Stream of classes to be used as plugins. MetaPlugin handlers
     * are loaded via SPI. A MetaPlugin is required for registering any plugins.
     * If a plugin is registered by a MetaPlugin, any accessible static final
     * methods with the annotation Plugin.OnLoad will execute.
     *
     * @param pluginStream the stream to handle.
     */
    public void scan(final Stream<Class<?>> pluginStream) {
        pluginStream
                .filter(plugin -> plugin.isAnnotationPresent(Plugin.class))
                .map(MetaClass::new)
                .map(metaClass -> Arrays.stream(metaClass.clazz.getFields())
                .filter(PluginScanner::isStaticFinal)
                .filter(field -> field.isAnnotationPresent(Plugin.Description.class))
                .map(PluginScanner::getField)
                .findFirst()
                .map(metaClass::withDescription)
                .orElse(metaClass))
                .map(metaClass -> Arrays.stream(metaClass.clazz.getFields())
                .filter(PluginScanner::isStaticFinal)
                .filter(field -> field.isAnnotationPresent(Plugin.Name.class))
                .map(PluginScanner::getField)
                .findFirst()
                .map(metaClass::withDescription)
                .orElse(metaClass))
                .filter(metaPlugin -> process(metaPlugins, metaPlugin))
                .map(metaPlugin -> metaPlugin.clazz)
                .flatMap(metaPlugin -> Arrays.stream(metaPlugin.getMethods()))
                .filter(PluginScanner::isStaticFinal)
                .forEach(PluginScanner::invokeMethod);
    }

    private static boolean isStaticFinal(final Field field) {
        final int mods = field.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;
        final boolean isFinal = (mods & Modifier.FINAL) != 0;

        return isStatic && isFinal && field.isAccessible();
    }

    private static boolean isStaticFinal(final Method method) {
        final int mods = method.getModifiers();
        final boolean isStatic = (mods & Modifier.STATIC) != 0;
        final boolean isFinal = (mods & Modifier.FINAL) != 0;

        return isStatic && isFinal && method.isAccessible();
    }

    private static void invokeMethod(final Method method) {
        try {
            method.invoke(null);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to invoke method: " + method.getName(), ex);
        }
    }

    private static String getField(final Field field) {
        try {
            return field.get(null).toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to access field: " + field.getName(), ex);
        }
    }

    private static boolean process(final List<MetaPlugin> handler, final MetaClass plugin) {
        return handler.stream()
                .filter(h -> h.register(plugin))
                .findFirst()
                .isPresent();
    }
}
