/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.plugin;

/**
 *
 * @author Robert
 * @param <BaseType>
 */
public class BasicPluginHandler<BaseType> extends AbstractPluginHandler<BaseType>{
    private final PluginManager<String, BaseType> pm = new PluginManager<>();
    private final Class<BaseType> baseClass;

    public BasicPluginHandler(Class<BaseType> baseClass) {
        this.baseClass = baseClass;
    }

    @Override
    protected PluginManager<String, BaseType> getPluginManager() {
        return pm;
    }

    @Override
    public boolean supportsType(Class theType) {
        return baseClass.isAssignableFrom(theType);
    }

}
