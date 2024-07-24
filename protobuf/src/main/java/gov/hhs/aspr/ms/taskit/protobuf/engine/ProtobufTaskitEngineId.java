package gov.hhs.aspr.ms.taskit.protobuf.engine;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;

/** 
 * TaskitEngineIds for the JSON and Binary ProtobufTaskitEngines.
 */
public final class ProtobufTaskitEngineId implements TaskitEngineId {
    public static final TaskitEngineId JSON_ENGINE_ID = new ProtobufTaskitEngineId();
    public static final TaskitEngineId BINARY_ENGINE_ID = new ProtobufTaskitEngineId();

    private ProtobufTaskitEngineId() {
    }
}
