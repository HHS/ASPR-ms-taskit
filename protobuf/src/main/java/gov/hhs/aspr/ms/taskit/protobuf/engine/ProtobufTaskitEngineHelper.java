package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

/**
 * This is a helper class that helps a ProtobufTaskitEngine with various utility
 * operations
 * <p>
 * package access for testing
 */
final class ProtobufTaskitEngineHelper {
    private ProtobufTaskitEngineHelper() {
    }

    /**
     * given a Class ref to a Protobuf Message, get the defaultInstance of it
     * <p>
     * package access for testing and use in ProtobufTaskitEngines
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
     * <p>
     * package access for testing and use in ProtobufTaskitEngines
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
     * <p>
     * package access for testing and use in ProtobufTaskitEngines
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
