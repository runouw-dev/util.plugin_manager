/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.plugin;

import java.util.Optional;

/**
 *
 * @author zmichaels
 */
public interface MetaPlugin {
    boolean register(MetaClass plugin);     
    
    <T> boolean supportsType(Class<T> theType);
    
    <T> Optional<T> newInstance(String id);
}
