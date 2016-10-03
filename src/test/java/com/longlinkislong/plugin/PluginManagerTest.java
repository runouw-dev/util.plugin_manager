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

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zmichaels
 */
public class PluginManagerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManagerTest.class);

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
    
    private PluginManager<String, SimplePlugin> plugins;       
    
    @Before
    public void setUp() throws ClassNotFoundException {
        this.plugins = new PluginManager<>();
        this.plugins.registerSelector("simpleplugin.SimplePluginSelector");
        //this.plugins.registerSelector(simpleplugin.SimplePluginSelector.getInstance());
    }
    
    @Test
    public void testGetInstance() throws ClassNotFoundException {
        final SimplePlugin plugin0 = this.plugins.getImplementation("HelloWorld");
        
        assertEquals(plugin0.getClass(), Class.forName("simpleplugin.HelloWorldPlugin"));
    }
    
    @Test
    public void testUniqueGetInstance() {
        final SimplePlugin plugin0 = this.plugins.getImplementation("HelloWorld");
        final SimplePlugin plugin1 = this.plugins.getImplementation("HelloWorld");
        
        assertNotSame(plugin0, plugin1);
    }
    
    @Test
    public void testListSupported() {
        List<String> supported = this.plugins.listPlugins();

        LOGGER.info("Supported: {}", supported);
    }

    @Test
    public void testConstructor() {
        final String msg = "Hello, World!";
        final SimplePlugin hello = this.plugins.getImplementation("Greeting", msg);

        assertEquals(msg, hello.toString());
    }
}
