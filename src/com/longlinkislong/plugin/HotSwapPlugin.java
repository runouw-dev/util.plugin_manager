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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A HotSwapPlugin is a type of object designed to wrap a plugin object that can
 * be replaced with a different instance sometime in the future.
 *
 * @author zmichaels
 * @param <PluginType> the type of plugin.
 * @since 15.07.28
 */
public class HotSwapPlugin<PluginType> {

    private PluginType currentObj;

    /**
     * Returns the Type Name of the current plugin.
     *
     * @return the Type Name.
     * @since 15.07.28
     */
    public String getPluginType() {
        return this.currentObj.getClass().getTypeName();
    }

    private final Map<String, MethodPlugin> methods = new HashMap<>();

    /**
     * Constructs a HotSwapPlugin for the specified type.
     *
     * @param initial the initial instance of the object.
     * @since 15.07.28
     */
    public HotSwapPlugin(final PluginType initial) {
        this.currentObj = Objects.requireNonNull(initial);
    }

    /**
     * Swaps the plugin object for a new plugin object.
     *
     * @param newPlugin the new plugin.
     * @since 15.07.28
     */
    public synchronized void swapPlugin(final PluginType newPlugin) {
        final PluginType oldPlugin = this.currentObj;

        this.currentObj = Objects.requireNonNull(newPlugin);

        try {
            for (Method method : newPlugin.getClass().getMethods()) {
                if (method.getName().equals("upgrade")) {
                    final Class<?>[] params = method.getParameterTypes();

                    if (params.length == 0) {
                        method.invoke(newPlugin);
                        return;
                    } else {
                        final Class<?> param = method.getParameterTypes()[0];
                        
                        // scan for suitable upgrade function based on interfaces
                        for(Class<?> test : oldPlugin.getClass().getInterfaces()) {
                            // try the parent of each interface                            
                            while(test != null) {
                                if(param == test) {
                                    method.invoke(newPlugin, oldPlugin);
                                    return;
                                }
                                test = test.getSuperclass();
                            }
                        }
                        
                        // scane for suitable upgrade function based on 
                        Class<?> test = oldPlugin.getClass();                                                
                        while (test != null) {                                                        
                            if (param == test) {                                
                                method.invoke(newPlugin, oldPlugin);
                                return;
                            }
                            test = test.getSuperclass();
                        }                                                
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new PluginException("Unable to call upgrade function!", ex);
        }
    }

    private synchronized MethodPlugin fetchMethod(final String name, final Class<?>[] pTypes) {
        final String desc = this.getDescriptor(name, pTypes);

        try {
            if (this.methods.containsKey(desc)) {
                final MethodPlugin method = this.methods.get(desc);

                return method.obj.get() == this.currentObj
                        ? method
                        : new MethodPlugin(this.currentObj, name, pTypes);
            } else {
                final MethodPlugin method = new MethodPlugin(this.currentObj, name, pTypes);

                this.methods.put(desc, method);

                return method;
            }
        } catch (NoSuchMethodException ex) {
            throw new PluginException("Unable to fetch method: " + desc, ex);
        }
    }

    /**
     * Invokes the specified method.
     *
     * @param method the method to invoke
     * @param params the parameters to pass
     * @return the result of the operation.
     * @throws PluginException if the method could not be executed.
     * @since 15.07.28
     */
    public Object invoke(final String method, final Object... params) throws PluginException {
        final Class<?>[] pTypes = Arrays
                .stream(params)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);

        final MethodPlugin pMethod = this.fetchMethod(method, pTypes);

        try {
            return pMethod.invoke(params);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new PluginException("Unable to invoke method: " + pMethod.descriptor, ex);
        }
    }

    private String getDescriptor(final String name, final Class<?>[] pTypes) {
        final StringBuilder desc = new StringBuilder();

        desc.append(name);
        desc.append("[");

        for (int i = 0; i < pTypes.length; i++) {
            desc.append("L");
            desc.append(pTypes[i].getTypeName());

            if (i < pTypes.length - 1) {
                desc.append(",");
            }
        }

        return desc.toString();
    }

    private class MethodPlugin {

        private final WeakReference<PluginType> obj;
        private final Method method;
        private final String descriptor;

        MethodPlugin(final PluginType obj, String name, Class<?>[] pTypes) throws NoSuchMethodException {
            this.obj = new WeakReference(obj);
            this.method = obj.getClass().getDeclaredMethod(name, pTypes);
            this.descriptor = getDescriptor(name, pTypes);
        }

        Object invoke(Object[] params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return this.method.invoke(this.obj.get(), params);
        }
    }
}
