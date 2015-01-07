package com.longlinkislong.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
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
public class PluginManager<Key, Implementation> {

    private final Map<Key, Class<? extends Implementation>> implementations = new HashMap<>();
    private final PluginSelector<Key, Implementation> selector;
    private Implementation selectedImpl;

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
        final Class<? extends PluginSelector<Key, Implementation>> implSelector
                = (Class<? extends PluginSelector<Key, Implementation>>) Class.forName(selectorDef);

        this.selector = getImplementation(implSelector).orElseThrow(NullPointerException::new);
        this.selector.registerImplements(this.implementations);
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
        this.selector = selector;
        this.selector.registerImplements(this.implementations);
    }

    /**
     * Retrieves either the implementation of the singleton instance or a new
     * instance of the object in that order.
     *
     * @param key the lookup key to use.
     * @return the implementation, if it exists.
     * @since 14.12.29
     */
    public Optional<? extends Implementation> getImplementation(final Key key) {
        final Class<? extends Implementation> def = this.implementations.get(key);

        return getImplementation(def);
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
        this.selectedImpl = this.getImplementation(this.selector.getPreferred())
                .orElseThrow(NullPointerException::new);

        return this.selectedImpl;
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
        if (this.selectedImpl == null) {
            return this.selectPreferred();
        } else {
            return this.selectedImpl;
        }
    }

    private static <Type> Type getImplementationFromSingleton(final Class<Type> def) {
        try {            
            final Method singletonGetter = def.getMethod("getInstance");            
            final Type impl = (Type) singletonGetter.invoke(null);

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

    private static <Type> Type getImplementationFromNewInstance(final Class<Type> def) {
        try {
            for (Constructor c : def.getConstructors()) {
                if (c.getParameterCount() == 0) {
                    return (Type) c.newInstance();
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
     * @return An Optional that may contain an instance of the object.
     * @since 14.12.29
     */
    public static <Type> Optional<Type> getImplementation(final Class<Type> def) {
        Type impl = getImplementationFromSingleton(def);

        if (impl == null) {            
            impl = getImplementationFromField(def);
        }

        if (impl == null) {            
            impl = getImplementationFromNewInstance(def);
        }

        if (impl == null) {
            try {                
                impl = def.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                impl = null;
            }
        }

        return Optional.ofNullable(impl);
    }
}
