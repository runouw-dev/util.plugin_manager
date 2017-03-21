/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spitest;

import com.longlinkislong.plugin.Plugin;

/**
 *
 * @author zmichaels
 */
@Plugin
public class Dog implements Animal {

    @Plugin.Lookup
    public static final String PLUGIN_ID = "plugins:dog";
    
    @Override
    public String say() {
        return "woof";
    }
    
}
