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

import org.junit.Assert;
import org.junit.Test;
import simpleplugin.BaseUpgradablePlugin;
import simpleplugin.GoodbyeWorldPlugin;
import simpleplugin.HelloWorldPlugin;
import simpleplugin.Stage2UpgradablePlugin;

/**
 *
 * @author zmichaels
 */
public class PluginSwapTest {

    private final PluginManager<String, SimplePlugin> manager = new PluginManager<>();
    private final HotSwapPlugin<SimplePlugin> greeting;

    public PluginSwapTest() {
        this.manager.registerSelector(PluginSelector.singletonSelector("Hello", HelloWorldPlugin.class));
        this.manager.registerSelector(PluginSelector.singletonSelector("Goodbye", GoodbyeWorldPlugin.class));
        this.greeting = new HotSwapPlugin(this.manager.getImplementation("Hello"));
    }

    @Test
    public void testPluginSwap() {
        // test swapping a plugin
        
        System.out.println("----------------test swap ---------------------");
        Assert.assertEquals("Hello World!", this.greeting.invoke("toString"));
        this.greeting.swapPlugin(this.manager.getImplementation("Goodbye"));
        Assert.assertEquals("Goodbye World!", this.greeting.invoke("toString"));
    }

    @Test
    public void testExternalUpdate() {
        // test swapping plugins with multiple threads
        
        System.out.println("------------- test external update ------------------");
        new Thread(() -> {
            try {
                Thread.sleep(30);
                this.greeting.swapPlugin(this.manager.getImplementation("Goodbye"));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();

        while (true) {
            try {
                final String msg = this.greeting.invoke("toString").toString();

                System.out.println(msg);

                if (msg.equals("Goodbye World!")) {
                    break;
                } else {
                    Assert.assertEquals("Hello World!", msg);
                }

                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void testOverwrite() {
        // test hotswap
        
        System.out.println("---------------- test overwrite ------------------");
        final PluginManager<String, SimplePlugin> mgr = new PluginManager();
        
        mgr.registerSelector(PluginSelector.singletonSelector("test", HelloWorldPlugin.class));
        
        final HotSwapPlugin<SimplePlugin> plugin = mgr.getHotSwapImplementation("test");        
        
        Assert.assertEquals("Hello World!", plugin.invoke("toString"));        
                
        mgr.registerSelector(PluginSelector.singletonSelector("test", GoodbyeWorldPlugin.class));       
        mgr.rebuildSelector();        
        
        Assert.assertEquals("Goodbye World!", plugin.invoke("toString"));
    }
    
    @Test
    public void testUpgrade() {
        // test hotswap upgrade
        
        System.out.println("-------------------- test upgrade ------------------");
        
        final PluginManager<String, SimplePlugin> mgr = new PluginManager<>();
        
        mgr.registerSelector(PluginSelector.singletonSelector("test", BaseUpgradablePlugin.class));
        
        final HotSwapPlugin<SimplePlugin> plugin = mgr.getHotSwapImplementation("test");
        
        Assert.assertEquals("base", plugin.invoke("toString"));
        
        mgr.registerSelector(PluginSelector.singletonSelector("test", Stage2UpgradablePlugin.class));
        mgr.rebuildSelector();
        
        Assert.assertEquals("base_upgrade", plugin.invoke("toString"));
    }
}
