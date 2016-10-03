/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleplugin;

import com.longlinkislong.plugin.SimplePlugin;

/**
 *
 * @author zmichaels
 */
public class GreetingPlugin implements SimplePlugin {
    private final String greeting;

    public GreetingPlugin(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String toString() {
        return this.greeting;
    }
}
