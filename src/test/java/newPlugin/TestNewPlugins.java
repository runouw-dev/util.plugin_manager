/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newPlugin;

import com.longlinkislong.plugin.BasicPluginHandler;
import com.longlinkislong.plugin.Plugin;
import com.longlinkislong.plugin.PluginHandler;
import com.longlinkislong.plugin.PluginScanner;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Robert
 */
public class TestNewPlugins {
    public interface Animal {
        String say();
    }
    
    @Plugin
    public static class Cat implements Animal{

        @Plugin.Lookup
        public static final String PLUGIN_ID = "Cat";
        
        public Cat() {
        }
        
        
        @Override
        public String say() { return "Meow"; }
    }
    
    @Plugin
    public static class Dog implements Animal{
        
        @Plugin.Lookup
        public static final String PLUGIN_ID = "Dog";

        public Dog() {
        }
        
        
        @Override
        public String say() { return "Woof"; }
    }
    
    @Test
    public void Test1(){
        PluginScanner scanner = new PluginScanner();
        scanner.addMetaPlugin(new BasicPluginHandler(Animal.class));
        
        scanner.scan(Cat.class, Dog.class);
        
        PluginHandler handler = scanner.getPluginHandler(Animal.class).get();
        
        Animal cat = scanner.newInstance(Animal.class, "Cat").get();
        Animal dog = scanner.newInstance(Animal.class, "Dog").get();
        
        assertEquals(cat.say(), "Meow");
        assertEquals(dog.say(), "Woof");
    }
}
