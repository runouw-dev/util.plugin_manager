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

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author zmichaels
 */
public class ComplexPluginSelectorTest {
    private PluginManager<String, SimplePlugin> plugins;          
    
    @Before
    public void setUp() throws ClassNotFoundException {
        final PluginSelectorBuilder<String, SimplePlugin> pBuilder 
                = new PluginSelectorBuilder<>();
        
        pBuilder.join("simpleplugin.SimplePluginSelector")
                .join("simpleplugin.IndexedPluginSelector");
        
        this.plugins = new PluginManager<>(pBuilder);
    }
    
    @Test
    public void testListPlugins() {
        final List<String> expectedList = new ArrayList<>();
        
        expectedList.add("HelloWorld");
        expectedList.add("IndexedPlugin");
        
        assertEquals(expectedList, this.plugins.listPlugins());
    }
    
    @Test
    public void testComplexInitiate() {
        final SimplePlugin plugin0 = this.plugins.getImplementation("HelloWorld");
        final SimplePlugin plugin1 = this.plugins.getImplementation("IndexedPlugin");
        final SimplePlugin plugin2 = this.plugins.getImplementation("IndexedPlugin");
        
        System.out.println(plugin0);
        System.out.println(plugin1);
        System.out.println(plugin2);
        
        assertNotSame(plugin0.getClass(), plugin1.getClass());
        assertEquals(plugin1.getClass(), plugin2.getClass());
    }
}
