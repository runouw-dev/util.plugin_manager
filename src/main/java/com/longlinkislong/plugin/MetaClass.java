/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.plugin;

/**
 *
 * @author zmichaels
 */
public final class MetaClass {
    public final String id;
    public final String name;
    public final String description;
    public final Class<?> clazz;
    
    public MetaClass(final Class<?> clazz) {
        this(clazz, clazz.getSimpleName(), clazz.getName(), "");
    }
        
    public MetaClass(final Class<?> clazz, final String id, final String name, final String desc) {
        this.clazz = clazz;
        this.id = id;
        this.name = name;
        this.description = desc;
    }        
    
    public MetaClass withId(final String id) {
        return new MetaClass(clazz, id, name, description);
    }
    
    public MetaClass withName(final String name) {
        return new MetaClass(clazz, id, name, description);
    }
    
    public MetaClass withDescription(final String description) {
        return new MetaClass(clazz, id, name, description);
    }
    
    @Override
    public String toString() {
        return String.format("MetaClass [id=%s name=%s desc=%s]", id, name, description);
    }
}
