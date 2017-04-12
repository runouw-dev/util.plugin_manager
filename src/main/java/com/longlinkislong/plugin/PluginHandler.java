/*
 * Copyright (c) 2017, zmichaels
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * PluginHandler defines the needed functionality for Plugin systems
 *
 * @author zmichaels
 */
public interface PluginHandler {

    /**
     * Attempts to register a PluginDescriptor as a plugin
     *
     * @param plugin the PluginDescriptor to register
     * @return true if the PluginDescriptor was registered by this PluginHandler
     */
    boolean register(PluginDescriptor plugin);

    /**
     * Checks if the supplied type is handled by this PluginHandler
     *
     * @param <T> the type to handle
     * @param theType the class definition
     * @return true if the PluginHandler handles the type.
     */
    <T> boolean supportsType(Class<T> theType);

    /**
     * Attempts to create a new instance of the specified ID
     *
     * @param <T> the type to attempt to cast to. This exists mostly for
     * syntactical sugar.
     * @param id the plugin id.
     * @param params
     * @return the new instance. May return empty if no plugins were registered
     * with the given id.
     */
    <T> Optional<T> newInstance(String id, Object... params);

    /**
     * List all plugins registered with this PluginHander.
     * @return collection of each PluginDescriptor.
     */
    Collection<PluginDescriptor> listPlugins();
}
