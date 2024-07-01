package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.type.Date;

import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.input.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.AnyTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.DateTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.DoubleTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.EnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.FloatTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.IntegerTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.LongTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.StringTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.UIntegerTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translationSpecs.ULongTranslationSpec;

/**
 * This is a helper class that encompasses all of the primitive translation
 * specs needed for translating to/from the Protobuf {@link Any} type.
 */
class ProtobufTaskitEngineHelper {
    ProtobufTaskitEngineHelper() {
    }

    final BooleanTranslationSpec BOOLEAN_TRANSLATOR_SPEC = new BooleanTranslationSpec();
    final IntegerTranslationSpec INT32_TRANSLATOR_SPEC = new IntegerTranslationSpec();
    final UIntegerTranslationSpec UINT32_TRANSLATOR_SPEC = new UIntegerTranslationSpec();
    final LongTranslationSpec INT64_TRANSLATOR_SPEC = new LongTranslationSpec();
    final ULongTranslationSpec UINT64_TRANSLATOR_SPEC = new ULongTranslationSpec();
    final StringTranslationSpec STRING_TRANSLATOR_SPEC = new StringTranslationSpec();
    final FloatTranslationSpec FLOAT_TRANSLATOR_SPEC = new FloatTranslationSpec();
    final DoubleTranslationSpec DOUBLE_TRANSLATOR_SPEC = new DoubleTranslationSpec();
    final DateTranslationSpec DATE_TRANSLATOR_SPEC = new DateTranslationSpec();
    final EnumTranslationSpec ENUM_TRANSLATOR_SPEC = new EnumTranslationSpec();
    final AnyTranslationSpec ANY_TRANSLATOR_SPEC = new AnyTranslationSpec();

    /**
     * Returns a set of Protobuf Message {@link Descriptor}s for each of the
     * Primitive TranslationSpecs. A Descriptor is to a Protobuf Message as Class is
     * to a Java Object.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their Descriptors are exclusively used to facilitate translating to/from a
     * Protobuf {@link Any} type
     */
    Set<Descriptor> getPrimitiveDescriptors() {
        Set<Descriptor> set = new LinkedHashSet<>();

        set.add(BoolValue.getDefaultInstance().getDescriptorForType());
        set.add(Int32Value.getDefaultInstance().getDescriptorForType());
        set.add(UInt32Value.getDefaultInstance().getDescriptorForType());
        set.add(Int64Value.getDefaultInstance().getDescriptorForType());
        set.add(UInt64Value.getDefaultInstance().getDescriptorForType());
        set.add(StringValue.getDefaultInstance().getDescriptorForType());
        set.add(FloatValue.getDefaultInstance().getDescriptorForType());
        set.add(DoubleValue.getDefaultInstance().getDescriptorForType());
        set.add(Date.getDefaultInstance().getDescriptorForType());
        set.add(WrapperEnumValue.getDefaultInstance().getDescriptorForType());

        return set;
    }

    /**
     * Returns a set of {@link ProtobufTranslationSpec}s that includes each of the
     * Primitive TranslationSpecs.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * are exclusively used to facilitate translating to/from a Protobuf {@link Any}
     * type
     */
    Set<ProtobufTranslationSpec<?, ?>> getPrimitiveTranslatorSpecs() {
        Set<ProtobufTranslationSpec<?, ?>> set = new LinkedHashSet<>();

        set.addAll(getPrimitiveInputTranslatorSpecMap().values());
        set.addAll(getPrimitiveObjectTranslatorSpecMap().values());

        return set;
    }

    /**
     * Returns a map of typeUrl to Class that includes each of the Primitive
     * TranslationSpecs.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their typeUrls are exclusively used to facilitate translating to/from a
     * Protobuf {@link Any} type
     */
    Map<String, Class<?>> getPrimitiveTypeUrlToClassMap() {
        Map<String, Class<?>> map = new LinkedHashMap<>();

        map.put(BoolValue.getDefaultInstance().getDescriptorForType().getFullName(),
                BOOLEAN_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(Int32Value.getDefaultInstance().getDescriptorForType().getFullName(),
                INT32_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(UInt32Value.getDefaultInstance().getDescriptorForType().getFullName(),
                UINT32_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(Int64Value.getDefaultInstance().getDescriptorForType().getFullName(),
                INT64_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(UInt64Value.getDefaultInstance().getDescriptorForType().getFullName(),
                UINT64_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(StringValue.getDefaultInstance().getDescriptorForType().getFullName(),
                STRING_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(FloatValue.getDefaultInstance().getDescriptorForType().getFullName(),
                FLOAT_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(DoubleValue.getDefaultInstance().getDescriptorForType().getFullName(),
                DOUBLE_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(Date.getDefaultInstance().getDescriptorForType().getFullName(),
                DATE_TRANSLATOR_SPEC.getInputObjectClass());
        map.put(WrapperEnumValue.getDefaultInstance().getDescriptorForType().getFullName(),
                ENUM_TRANSLATOR_SPEC.getInputObjectClass());

        return map;
    }

    /**
     * Returns a map of {@link Class} to {@link ProtobufTranslationSpec} that
     * includes each of the Primitive TranslationSpecs. This map is exclusively a
     * map of the inputObjectClass to the TranslationSpec.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their inputObjectClasses are exclusively used to facilitate translating
     * to/from a Protobuf {@link Any} type
     */
    Map<Class<?>, ProtobufTranslationSpec<?, ?>> getPrimitiveInputTranslatorSpecMap() {
        Map<Class<?>, ProtobufTranslationSpec<?, ?>> map = new LinkedHashMap<>();

        map.put(BOOLEAN_TRANSLATOR_SPEC.getInputObjectClass(), BOOLEAN_TRANSLATOR_SPEC);
        map.put(INT32_TRANSLATOR_SPEC.getInputObjectClass(), INT32_TRANSLATOR_SPEC);
        map.put(UINT32_TRANSLATOR_SPEC.getInputObjectClass(), UINT32_TRANSLATOR_SPEC);
        map.put(INT64_TRANSLATOR_SPEC.getInputObjectClass(), INT64_TRANSLATOR_SPEC);
        map.put(UINT64_TRANSLATOR_SPEC.getInputObjectClass(), UINT64_TRANSLATOR_SPEC);
        map.put(STRING_TRANSLATOR_SPEC.getInputObjectClass(), STRING_TRANSLATOR_SPEC);
        map.put(FLOAT_TRANSLATOR_SPEC.getInputObjectClass(), FLOAT_TRANSLATOR_SPEC);
        map.put(DOUBLE_TRANSLATOR_SPEC.getInputObjectClass(), DOUBLE_TRANSLATOR_SPEC);
        map.put(DATE_TRANSLATOR_SPEC.getInputObjectClass(), DATE_TRANSLATOR_SPEC);
        map.put(ENUM_TRANSLATOR_SPEC.getInputObjectClass(), ENUM_TRANSLATOR_SPEC);
        map.put(ANY_TRANSLATOR_SPEC.getInputObjectClass(), ANY_TRANSLATOR_SPEC);

        return map;
    }

    /**
     * Returns a map of {@link Class} to {@link ProtobufTranslationSpec} that
     * includes each of the Primitive TranslationSpecs. This map is exclusively a
     * map of the appObjectClass to the TranslationSpec.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their appObjectClasses are exclusively used to facilitate translating
     * to/from a Protobuf {@link Any} type
     */
    Map<Class<?>, ProtobufTranslationSpec<?, ?>> getPrimitiveObjectTranslatorSpecMap() {
        Map<Class<?>, ProtobufTranslationSpec<?, ?>> map = new LinkedHashMap<>();

        // no java version of unsigned int nor unsigned long
        map.put(BOOLEAN_TRANSLATOR_SPEC.getAppObjectClass(), BOOLEAN_TRANSLATOR_SPEC);
        map.put(INT32_TRANSLATOR_SPEC.getAppObjectClass(), INT32_TRANSLATOR_SPEC);
        map.put(INT64_TRANSLATOR_SPEC.getAppObjectClass(), INT64_TRANSLATOR_SPEC);
        map.put(STRING_TRANSLATOR_SPEC.getAppObjectClass(), STRING_TRANSLATOR_SPEC);
        map.put(FLOAT_TRANSLATOR_SPEC.getAppObjectClass(), FLOAT_TRANSLATOR_SPEC);
        map.put(DOUBLE_TRANSLATOR_SPEC.getAppObjectClass(), DOUBLE_TRANSLATOR_SPEC);
        map.put(DATE_TRANSLATOR_SPEC.getAppObjectClass(), DATE_TRANSLATOR_SPEC);
        map.put(ENUM_TRANSLATOR_SPEC.getAppObjectClass(), ENUM_TRANSLATOR_SPEC);

        return map;
    }

    /**
     * given a Class ref to a Protobuf Message, get the defaultInstance of it
     */
    static <U extends Message> U getDefaultMessage(Class<U> classRef) {
        try {
            Method method = classRef.getMethod("getDefaultInstance");
            Object obj = method.invoke(null);
            return classRef.cast(obj);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * given a Class ref to a ProtocolMessageEnum, get the default value for it,
     * enum number 0 within the proto enum
     */
    static <U extends ProtocolMessageEnum> U getDefaultEnum(Class<U> classRef) {
        try {
            Method method = classRef.getMethod("forNumber", int.class);
            Object obj = method.invoke(null, 0);
            return classRef.cast(obj);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * uses reflection to obtain a builder for the given classRef
    */
    static <U> Message.Builder getBuilderForMessage(Class<U> classRef) {

        Method[] messageMethods = classRef.getDeclaredMethods();

        List<Method> newBuilderMethods = new ArrayList<>();
        for (Method method : messageMethods) {
            if (method.getName().equals("newBuilder")) {
                newBuilderMethods.add(method);
            }
        }

        if (newBuilderMethods.isEmpty()) {
            throw new RuntimeException("The method \"newBuilder\" does not exist");
        }

        for (Method method : newBuilderMethods) {
            if (method.getParameterCount() == 0) {
                try {
                    return (com.google.protobuf.Message.Builder) method.invoke(null);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new RuntimeException(
                "\"newBuilder\" method exists, but it requires arguments, when it is expected to require 0 arguments");
    }
}
