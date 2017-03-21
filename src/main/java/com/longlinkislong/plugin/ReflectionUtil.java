/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.longlinkislong.plugin;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for reflection.
 *
 * @author zmichaels
 */
public final class ReflectionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
    }

    /**
     * Checks if the field is static-final.
     *
     * @param field the field.
     * @return true if the field has both the static and final modifiers.
     */
    public static boolean isStaticFinal(final Field field) {
        final int mods = field.getModifiers();

        return Modifier.isFinal(mods) && Modifier.isStatic(mods);
    }

    /**
     * Checks if the field is static.
     *
     * @param field the field.
     * @return true if the field has the static modifier.
     */
    public static boolean isStatic(final Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * Checks if the field is final.
     *
     * @param field the field.
     * @return true if the field has the final modifier.
     */
    public static boolean isFinal(final Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    /**
     * Checks if the method is static.
     *
     * @param method the method.
     * @return true if the method has the static modifier.
     */
    public static boolean isStatic(final Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * Attempts to set the static Object field. All exceptions are logged.
     *
     * @param <ValueT> the value type.
     * @param field the field to assign.
     * @param value the value.
     * @return the value wrapped in an Optional. May be empty if the assignment
     * failed.
     */
    public static <ValueT> Optional<ValueT> setStaticObjectField(final Field field, final ValueT value) {
        try {
            field.set(null, value);
            return Optional.of(value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    /**
     * Attempts to set the static int field. All exceptions are logged.
     *
     * @param field the field.
     * @param value the value.
     * @return the value wrapped in an OptionalInt. May be empty if the
     * assignment failed.
     */
    public static OptionalInt setStaticIntField(final Field field, final int value) {
        try {
            field.setInt(null, value);
            return OptionalInt.of(value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalInt.empty();
        }
    }

    /**
     * Attempts to set the static long field. All exceptions are logged.
     *
     * @param field the field.
     * @param value the value.
     * @return the value wrapped in an OptionalLong. May be empty if the
     * assignment failed.
     */
    public static OptionalLong setStaticLongField(final Field field, final long value) {
        try {
            field.setLong(null, value);
            return OptionalLong.of(value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalLong.empty();
        }
    }

    /**
     * Attempts to set the static double field. All exceptions are logged.
     *
     * @param field the field.
     * @param value the value.
     * @return the value wrapped in an OptionalDouble. May be empty if the
     * assignment failed.
     */
    public static OptionalDouble setStaticDoubleField(final Field field, final double value) {
        try {
            field.setDouble(null, value);
            return OptionalDouble.of(value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalDouble.empty();
        }
    }

    /**
     * Curried form of setStaticIntField with the value being held as constant.
     *
     * @param value the value.
     * @return the curried form.
     */
    public static Consumer<Field> staticIntFieldSetter(final int value) {
        return field -> setStaticIntField(field, value);
    }

    /**
     * Curried form of setStaticLongField with the value being held as constant.
     *
     * @param value the value.
     * @return the curried form.
     */
    public static Consumer<Field> staticLongFieldSetter(final long value) {
        return field -> setStaticLongField(field, value);
    }

    /**
     * Curried form of setStaticDoubleField with the value being held as
     * constant.
     *
     * @param value the value.
     * @return the curried form.
     */
    public static Consumer<Field> staticDoubleFieldSetter(final double value) {
        return field -> setStaticDoubleField(field, value);
    }

    /**
     * Curried form of setStaticObjectField with the value being held as
     * constant.
     *
     * @param value the value.
     * @return the curried form.
     */
    public static Consumer<Field> staticObjectFieldSetter(final Object value) {
        return field -> setStaticObjectField(field, value);
    }

    /**
     * Attempts to retrieve a static Object value from a field. All exceptions
     * are logged.
     *
     * @param <ValueT> the value type.
     * @param field the field.
     * @return the value wrapped in an Optional. May return an empty Optional if
     * the operation failed.
     */
    public static <ValueT> Optional<ValueT> getStaticObjectField(final Field field) {
        try {
            return Optional.of((ValueT) field.get(null));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    /**
     * Attempts to retrieve a static int value from a field. All exceptions are
     * logged.
     *
     * @param field the field.
     * @return the int wrapped in an OptionalInt. May return an empty
     * OptionalInt if the operation failed.
     */
    public static OptionalInt getStaticIntField(final Field field) {
        try {
            return OptionalInt.of(field.getInt(null));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalInt.empty();
        }
    }

    /**
     * Attempts to retrieve a static long value from a field. All exceptions are
     * logged.
     *
     * @param field the field.
     * @return the long wrapped in an OptionalLong. May return an empty
     * OptionalLong if the operation failed.
     */
    public static OptionalLong getStaticLongField(final Field field) {
        try {
            return OptionalLong.of(field.getInt(null));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalLong.empty();
        }
    }

    /**
     * Attempts to retrieve a static double value from a field. All exceptions
     * are logged.
     *
     * @param field the field.
     * @return the long wrapped in an OptionalDouble. May return an empty
     * OptionalDouble if the operation failed.
     */
    public static OptionalDouble getStaticDoubleField(final Field field) {
        try {
            return OptionalDouble.of(field.getInt(null));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return OptionalDouble.empty();
        }
    }

    /**
     * Checks if the field is annotated with the given annotation.
     *
     * @param field the field.
     * @param anno the annotation.
     * @return true if the field is annotated with the specified annotation.
     */
    public static boolean hasAnnotation(final Field field, final Class<? extends Annotation> anno) {
        return field.isAnnotationPresent(anno);
    }

    /**
     * Checks if the method is annotated with the given annotation.
     *
     * @param method the method.
     * @param anno the annotation.
     * @return true if the method is annotated with the specified annotation.
     */
    public static boolean hasAnnotation(final Method method, final Class<? extends Annotation> anno) {
        return method.isAnnotationPresent(anno);
    }

    /**
     * Checks if the class is annotated with the given annotation.
     *
     * @param clazz the class.
     * @param anno the annotation.
     * @return true if the class is annotated with the specified annotation.
     */
    public static boolean hasAnnotation(final Class<?> clazz, final Class<? extends Annotation> anno) {
        return clazz.isAnnotationPresent(anno);
    }

    /**
     * Curried form of hasAnnotation with the annotation being held as contant.
     *
     * @param anno the annotation.
     * @return the curried form.
     */
    public static Predicate<Field> fieldAnnotationTest(final Class<? extends Annotation> anno) {
        return field -> hasAnnotation(field, anno);
    }

    /**
     * Curried form of hasAnnotation with the annotation being held as contant.
     *
     * @param anno the annotation.
     * @return the curried form.
     */
    public static Predicate<Method> methodAnnotationTest(final Class<? extends Annotation> anno) {
        return method -> hasAnnotation(method, anno);
    }

    /**
     * Curried form of hasAnnotation with the annotation being held as contant.
     *
     * @param anno the annotation.
     * @return the curried form.
     */
    public static Predicate<Class<?>> classAnnotationTest(final Class<? extends Annotation> anno) {
        return clazz -> hasAnnotation(clazz, anno);
    }

    /**
     * Attempts to unreflect a Method into a MethodHandle. All exceptions are
     * logged.
     *
     * @param method the method.
     * @return the MethodHandle wrapped in an Optional. May return an empty
     * Optional if access failed.
     */
    public static Optional<MethodHandle> unreflect(final Method method) {
        try {
            return Optional.of(MethodHandles.lookup().unreflect(method));
        } catch (IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    /**
     * Attempts to unreflect a field into a getter MethodHandle. All exceptions
     * are logged.
     *
     * @param field the field.
     * @return the MethodHandle wrapped in an Optional. May return an empty
     * Optional if access failed.
     */
    public static Optional<MethodHandle> unreflectGetter(final Field field) {
        try {
            return Optional.of(MethodHandles.lookup().unreflectGetter(field));
        } catch (IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    /**
     * Attempts to unreflect a field into a setter MethodHandle. All exceptions
     * are logged.
     *
     * @param field the field.
     * @return the MethodHandle wrapped in an Optional. May return an empty
     * Optional if access failed.
     */
    public static Optional<MethodHandle> unreflectSetter(final Field field) {
        try {
            return Optional.of(MethodHandles.lookup().unreflectSetter(field));
        } catch (IllegalAccessException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    /**
     * Sets the field as accessible. This will ignore JVM privileges for read or
     * write.
     *
     * @param field the field.
     * @return the field.
     */
    public static Field setAccessible(final Field field) {
        field.setAccessible(true);
        return field;
    }

    /**
     * Sets the method as accessible. This will ignore JVM privileges for
     * invoke.
     *
     * @param method the method.
     * @return the method.
     */
    public static Method setAccessible(final Method method) {
        method.setAccessible(true);
        return method;
    }

    /**
     * Invokes a static method and retrieves any result. All exceptions will be
     * logged.
     *
     * @param <ReturnT> the return type.
     * @param method the method to invoke.
     * @param params optional parameters.
     * @return the return type if any. May return an empty Optional if the
     * method returned null or there was no return or if an exception occurred.
     */
    public static <ReturnT> Optional<ReturnT> invokeStatic(final Method method, final Object... params) {
        try {
            return Optional.ofNullable((ReturnT) method.invoke(null, params));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }
}
