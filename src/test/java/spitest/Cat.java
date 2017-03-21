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
public class Cat implements Animal{

    @Plugin.Lookup
    public static final String ID = "plugins:cat";       
    
    @Override
    public String say() {
        return "meow";
    }
    
}
