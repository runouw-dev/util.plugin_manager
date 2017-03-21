/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spitest;

import com.longlinkislong.plugin.PluginDescriptor;
import com.longlinkislong.plugin.PluginScanner;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example of SPI implementation. The service implementation is specified in
 * test resources/META-INF.services
 *
 * @author zmichaels
 */
public class SPITest {    
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    @Test
    public void testBasic() {
        final PluginScanner scanner = new PluginScanner();
        final Logger logger = LoggerFactory.getLogger(SPITest.class);
        final List<PluginDescriptor> loadedPlugins = scanner.scan(Cat.class, Dog.class);
        
        logger.info("Loaded plugins: {}", loadedPlugins);        

        assertEquals("meow", scanner.newInstance(Animal.class, "plugins:cat").get().say());
        assertEquals("woof", scanner.newInstance(Animal.class, "plugins:dog").get().say());
    }

    @Test
    public void testUpdate() {
        final PluginScanner scanner = new PluginScanner();
        final Logger logger = LoggerFactory.getLogger(SPITest.class);
        final List<PluginDescriptor> loadedPlugins = scanner.scan(Cat.class, Dog.class);
        
        logger.info("Loaded plugins: {}", loadedPlugins);

        assertEquals("meow", scanner.newInstance(Animal.class, "plugins:cat").get().say());
        assertEquals("woof", scanner.newInstance(Animal.class, "plugins:dog").get().say());

        final List<PluginDescriptor> updatedPlugins = scanner.scan(DogModded.class);
        
        logger.info("Updated plugin: {}", updatedPlugins);

        assertFalse("woof".equals(scanner.newInstance(Animal.class, "plugins:dog").get().say()));
    }
}
