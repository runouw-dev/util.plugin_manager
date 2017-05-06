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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An immutable structure that holds additional data that might be used for the
 * PluginScanner.
 *
 * @author zmichaels
 */
public final class PluginDescriptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginDescriptor.class);
    /**
     * The lookup id. Used in initializing the plugin. The default value is the
     * class simple name.
     */
    public final String lookup;
    /**
     * The name of the plugin. The default value is the class name.
     */
    public final String name;
    /**
     * The description of the plugin. Default value is an empty String.
     */
    public final String description;
    /**
     * The class definition.
     */
    public final Class<?> clazz;

//    /**
//     * Uses reflection to retrieve the values of all static final fields
//     * annotated with the specified annotation. All fields will be set as
//     * accessible.
//     *
//     * @param anno the annotation to search for.
//     * @return the list of annotated values.
//     */
//    public List<Object> getAnnotatedStaticFinalFieldObjects(final Class<? extends Annotation> anno) {
//        return annotatedFields(anno)
//                .filter(ReflectionUtil::isStaticFinal)
//                .map(ReflectionUtil::setAccessible)
//                .map(ReflectionUtil::getStaticObjectField)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Retrieves a Stream of fields annotated with the specified annotation. The
//     * fields will not be set as accessible. This will also include non-static
//     * final fields.
//     *
//     * @param anno the annotation to search for.
//     * @return the stream of fields.
//     */
//    public Stream<Field> annotatedFields(final Class<? extends Annotation> anno) {
//        return Arrays.stream(this.clazz.getFields())
//                .filter(ReflectionUtil.fieldAnnotationTest(anno));
//    }
//
//    /**
//     * Retrieves a Stream of methods annotated with the specified annotation.
//     * The fields will not be set as accessible. This will also include
//     * non-static methods.
//     *
//     * @param anno the annotation to search for.
//     * @return the stream of methods.
//     */
//    public Stream<Method> annotatedMethods(final Class<? extends Annotation> anno) {
//        return Arrays.stream(this.clazz.getMethods())
//                .filter(ReflectionUtil.methodAnnotationTest(anno));
//    }
//
//    /**
//     * Returns a List of static Methods that were annotated with the given
//     * Annotation.
//     *
//     * @param anno the annotation
//     * @return the list of methods.
//     */
//    public List<Method> getAnnotatedStaticMethods(final Class<? extends Annotation> anno) {
//        return annotatedMethods(anno)
//                .filter(ReflectionUtil::isStatic)
//                .map(ReflectionUtil::setAccessible)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Assigns all annotated static Object fields that match the specified
//     * annotation.
//     *
//     * @param anno the annotation to test for.
//     * @param value the value to assign.
//     */
//    public void setAnnotatedStaticObjects(final Class<? extends Annotation> anno, final Object value) {
//        annotatedFields(anno)
//                .filter(ReflectionUtil::isStatic)
//                .map(ReflectionUtil::setAccessible)
//                .forEach(ReflectionUtil.staticObjectFieldSetter(value));
//    }
//
//    /**
//     * Assigns all annotated static int fields that match the specified
//     * annotation.
//     *
//     * @param anno the annotation to test for.
//     * @param value the value to assign.
//     */
//    public void setAnnotatedStaticInts(final Class<? extends Annotation> anno, final int value) {
//        annotatedFields(anno)
//                .filter(ReflectionUtil::isStatic)
//                .map(ReflectionUtil::setAccessible)
//                .forEach(ReflectionUtil.staticIntFieldSetter(value));
//    }
//
//    /**
//     * Assigns all annotated static long fields that match the specified
//     * annotation.
//     *
//     * @param anno the annotation to test for.
//     * @param value the value to assign.
//     */
//    public void setAnnotatedStaticLongs(final Class<? extends Annotation> anno, final long value) {
//        annotatedFields(anno)
//                .filter(ReflectionUtil::isStatic)
//                .map(ReflectionUtil::setAccessible)
//                .forEach(ReflectionUtil.staticLongFieldSetter(value));
//    }
//
//    /**
//     * Assigns all annotated static double fields that match the specified
//     * annotation.
//     *
//     * @param anno the annotation to test for.
//     * @param value the value to assign.
//     */
//    public void setAnnotatedStaticDoubles(final Class<? extends Annotation> anno, final double value) {
//        annotatedFields(anno)
//                .filter(ReflectionUtil::isStatic)
//                .map(ReflectionUtil::setAccessible)
//                .forEach(ReflectionUtil.staticDoubleFieldSetter(value));
//    }

    /**
     * Checks if the name field is currently the default value.
     *
     * @return true if the Name field was never changed.
     */
    public boolean isNameDefault() {
        return this.clazz.getName().equals(this.name);
    }

    /**
     * Checks if the lookup field is currently the default value.
     *
     * @return true if the Lookup field was never changed.
     */
    public boolean isLookupDefault() {
        return this.clazz.getSimpleName().equals(this.lookup);
    }

    /**
     * Checks if the description field is currently the default value.
     *
     * @return true if the Description field was never changed.
     */
    public boolean isDescriptionDefault() {
        return this.description.isEmpty();
    }

    /**
     * Merges the left hand side PluginDescriptor with the right hand side
     * PluginDescriptor. Changes will only be made when a field in the lhs
     * PluginDescriptor is still the default value.
     *
     * @param lhs the identity PluginDescriptor
     * @param rhs the difference PluginDescriptor
     * @return merged PluginDescriptor or the lhs instance if none of the values
     * in the lhs are default.
     */
    public static PluginDescriptor combine(final PluginDescriptor lhs, final PluginDescriptor rhs) {
        if (lhs.clazz != rhs.clazz) {
            throw new IllegalStateException("Cannot merge two different PluginDescriptors!");
        }

        PluginDescriptor out = lhs;

        if (lhs.isNameDefault()) {
            out = lhs.withName(rhs.name);
        }

        if (lhs.isLookupDefault()) {
            out = lhs.withLookup(rhs.lookup);
        }

        if (lhs.isDescriptionDefault()) {
            out = lhs.withDescription(rhs.description);
        }

        return out;
    }

    /**
     * Constructs a new MetaClass from the supplied Class. The lookup will be
     * initialized with the simple name, the name will be initialized with the
     * class name, and the description will be initialized as an empty String.
     *
     * @param clazz the Class instance.
     */
    public PluginDescriptor(final Class<?> clazz) {
        this(clazz, clazz.getSimpleName(), clazz.getName(), "");
    }

    /**
     * Constructs a new MetaClass.
     *
     * @param clazz the class definition.
     * @param lookup the lookup value.
     * @param name the name
     * @param desc the description
     */
    public PluginDescriptor(final Class<?> clazz, final String lookup, final String name, final String desc) {
        this.clazz = clazz;
        this.lookup = lookup;
        this.name = name;
        this.description = desc;
    }

    /**
     * Creates a new instance of this PluginDescriptor with the new Lookup
     * value.
     *
     * @param lookup the new lookup value.
     * @return the new PluginDescriptor.
     */
    public PluginDescriptor withLookup(final String lookup) {
        return new PluginDescriptor(clazz, lookup, name, description);
    }

    /**
     * Creates a new instance of this PluginDescriptor with the new name value.
     *
     * @param name the new name value.
     * @return the new PluginDescriptor.
     */
    public PluginDescriptor withName(final String name) {
        return new PluginDescriptor(clazz, lookup, name, description);
    }

    /**
     * Creates a new instance of this PluginDescriptor with the new discription
     * value.
     *
     * @param description the description value.
     * @return the new PluginDescriptor.
     */
    public PluginDescriptor withDescription(final String description) {
        return new PluginDescriptor(clazz, lookup, name, description);
    }

    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(512);

        out.append("PluginDescriptor [lookup=");
        out.append(this.lookup);
        out.append(" name=");
        out.append(this.name);

        if (this.isDescriptionDefault()) {
            // do nothing
        } else {
            out.append(" desc=");
            out.append(this.description);
        }

        out.append("]");

        return out.toString();
    }
}
