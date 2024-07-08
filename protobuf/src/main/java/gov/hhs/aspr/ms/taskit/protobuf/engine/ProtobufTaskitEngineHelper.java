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

import gov.hhs.aspr.ms.taskit.protobuf.objects.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DateTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DoubleTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.EnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.FloatTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.IntegerTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.LongTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.StringTranslationSpec;

/**
 * This is a helper class that encompasses all of the primitive translation
 * specs needed for translating to/from the Protobuf {@link Any} type.
 */
final class ProtobufTaskitEngineHelper {
    private ProtobufTaskitEngineHelper() {
    }

    /**
     * Returns a set of Protobuf Message {@link Descriptor}s for each of the
     * Primitive TranslationSpecs. A Descriptor is to a Protobuf Message as Class is
     * to a Java Object.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their Descriptors are exclusively used to facilitate translating to/from
     * a
     * Protobuf {@link Any} type
     */
    static Set<Descriptor> getPrimitiveDescriptors() {
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
    static Set<ProtobufTranslationSpec<?, ?>> getPrimitiveTranslationSpecs() {
        Set<ProtobufTranslationSpec<?, ?>> set = new LinkedHashSet<>();

        set.add(new BooleanTranslationSpec());
        set.add(new IntegerTranslationSpec());
        set.add(new LongTranslationSpec());
        set.add(new StringTranslationSpec());
        set.add(new FloatTranslationSpec());
        set.add(new DoubleTranslationSpec());
        set.add(new DateTranslationSpec());
        set.add(new EnumTranslationSpec());
        set.add(new AnyTranslationSpec());

        return set;
    }

    /**
     * Returns a map of typeUrl to Class that includes each of the Primitive
     * TranslationSpecs.
     * <li>Note: as mentioned in the Class javadoc, these Primitive TranslationSpecs
     * and their typeUrls are exclusively used to facilitate translating to/from a
     * Protobuf {@link Any} type
     */
    static Map<String, Class<?>> getPrimitiveTypeUrlToClassMap() {
        Map<String, Class<?>> map = new LinkedHashMap<>();

        map.put(BoolValue.getDefaultInstance().getDescriptorForType().getFullName(),
                BoolValue.class);
        map.put(Int32Value.getDefaultInstance().getDescriptorForType().getFullName(),
                Int32Value.class);
        map.put(Int64Value.getDefaultInstance().getDescriptorForType().getFullName(),
                Int64Value.class);
        map.put(StringValue.getDefaultInstance().getDescriptorForType().getFullName(),
                StringValue.class);
        map.put(FloatValue.getDefaultInstance().getDescriptorForType().getFullName(),
                FloatValue.class);
        map.put(DoubleValue.getDefaultInstance().getDescriptorForType().getFullName(),
                DoubleValue.class);
        map.put(Date.getDefaultInstance().getDescriptorForType().getFullName(),
                Date.class);
        map.put(WrapperEnumValue.getDefaultInstance().getDescriptorForType().getFullName(),
                WrapperEnumValue.class);

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
