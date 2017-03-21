/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spitest.spi;

import com.longlinkislong.plugin.BasicPluginHandler;
import spitest.Animal;

/**
 *
 * @author zmichaels
 */
public class AnimalHandler extends BasicPluginHandler {
    public AnimalHandler() {
        super(Animal.class);
    }
}
