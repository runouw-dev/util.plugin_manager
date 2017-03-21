/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spitest;

import com.longlinkislong.plugin.PluginScanner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

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

        scanner.scan(Cat.class, Dog.class);

        assertEquals("meow", scanner.newInstance(Animal.class, "plugins:cat").get().say());
        assertEquals("woof", scanner.newInstance(Animal.class, "plugins:dog").get().say());
    }

    @Test
    public void testUpdate() {
        final PluginScanner scanner = new PluginScanner();

        scanner.scan(Cat.class, Dog.class);

        assertEquals("meow", scanner.newInstance(Animal.class, "plugins:cat").get().say());
        assertEquals("woof", scanner.newInstance(Animal.class, "plugins:dog").get().say());

        scanner.scan(DogModded.class);

        assertFalse("woof".equals(scanner.newInstance(Animal.class, "plugins:dog").get().say()));
    }
}
