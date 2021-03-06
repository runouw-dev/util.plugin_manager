/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newPlugin;

import com.longlinkislong.plugin.BasicPluginHandler;
import com.longlinkislong.plugin.Plugin;
import com.longlinkislong.plugin.PluginScanner;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

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
    public static class CatModded implements Animal{

        @Plugin.Lookup
        public static final String PLUGIN_ID = "Cat";
        
        public CatModded() {
        }
        
        
        @Override
        public String say() { return "Bzzrrt"; }
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
    public void TestBasic(){
        PluginScanner scanner = new PluginScanner();
        scanner.addPluginHandler(new BasicPluginHandler(Animal.class));
        
        scanner.scan(Cat.class, Dog.class);
        
        Animal cat = scanner.newInstance(Animal.class, "Cat").get();
        Animal dog = scanner.newInstance(Animal.class, "Dog").get();
        
        assertEquals(cat.say(), "Meow");
        assertEquals(dog.say(), "Woof");
    }
    
    @Test
    public void TestUpdate(){
        PluginScanner scanner = new PluginScanner();
        scanner.addPluginHandler(new BasicPluginHandler(Animal.class));
        
        scanner.scan(Cat.class, Dog.class);
        
        Animal cat = scanner.newInstance(Animal.class, "Cat").get();
        Animal dog = scanner.newInstance(Animal.class, "Dog").get();
        
        assertEquals(cat.say(), "Meow");
        assertEquals(dog.say(), "Woof");
        
        // simulating the old plugin being updated by a new one
        // such as a run-time update and refresh
        scanner.scan(CatModded.class);
        
        cat = scanner.newInstance(Animal.class, "Cat").get();
        assertEquals(cat.say(), "Bzzrrt");
    }
}
