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

/**
 * A type of exception thrown by the PluginManager package.
 * @author zmichaels
 * @since 15.06.18
 */
@SuppressWarnings("serial")
public class PluginException extends RuntimeException {

    /**
     * Constructs a new PluginException with no message or cause.
     * @since 15.06.18
     */
    public PluginException() {
    }

    /**
     * Constructs a PluginException with a message and cause.
     * @param msg the description of the exception
     * @param cause the cause of the exception
     * @since 15.06.18
     */
    public PluginException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a PluginException with a message.
     * @param msg the description of the exception
     * @since 15.06.18
     */
    public PluginException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a PluginException with a specified cause.
     * @param cause the cause of the exception
     * @since 15.06.18
     */
    public PluginException(final Throwable cause) {
        super(cause);
    }
}
