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
package simpleplugin;

import com.longlinkislong.plugin.PluginSelector;
import com.longlinkislong.plugin.SimplePlugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zmichaels
 */
public final class SimplePluginSelector implements PluginSelector<String, SimplePlugin> {
    private final List<String> supported = new ArrayList<>(2);
    
    public static SimplePluginSelector getInstance() {
        return Holder.INSTANCE;
    }
    
    private static final class Holder {
        private static final SimplePluginSelector INSTANCE = new SimplePluginSelector();
        private Holder() {}
    }
    
    private SimplePluginSelector() {
        this.supported.add("HelloWorld");
        this.supported.add("Greeting");
    }
    
    @Override
    public List<String> getSupported() {
        return Collections.unmodifiableList(supported);
    }

    @Override
    public void registerImplements(Map<String, Class<? extends SimplePlugin>> pluginImpl) {
        pluginImpl.put("HelloWorld", HelloWorldPlugin.class);
        pluginImpl.put("Greeting", GreetingPlugin.class);
    }
    
}
