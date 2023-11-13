package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestResourceHelper;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestTranslationEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.translationSpecs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.translationSpecs.TestObjectTranslationSpec;
import util.annotations.UnitTestForCoverage;
import util.annotations.UnitTestMethod;
import util.errors.ContractException;

public class AT_TranslationController {

    Path basePath = TestResourceHelper.getResourceDir(this.getClass());
    Path filePath = TestResourceHelper.makeTestOutputDir(basePath);

    @Test
    @UnitTestForCoverage
    public void testValidateTranslationEngine() {
        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build()).buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NO_TRANSLATION_ENGINES, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines.values()) {
                translationController.translationEngines.put(translationEngine.getTranslationEngineType(), null);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines.values()) {
                translationController.translationEngines.put(translationEngine.getTranslationEngineType(),
                        translationEngine);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals("TranslationEngine has been built but has not been initialized.", runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines.values()) {
                translationEngine.init();
                translationController.translationEngines.put(translationEngine.getTranslationEngineType(),
                        translationEngine);
                translationController.translationEngineClassToTypeMap.put(translationEngine.getClass(), null);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TranslationEngine translationEngine : translationController.data.translationEngines.values()) {
                translationEngine.init();
                translationController.translationEngines.put(translationEngine.getTranslationEngineType(),
                        translationEngine);
            }
            translationController.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());
    }

    @Test
    @UnitTestForCoverage
    /*
     * purpose of this test is to show that if there isnt a valid TranslationEngine
     * class -> Translation Engine Type -> Translation Engine mapping, an exception
     * is thrown
     */
    public void testValidateTranslationEngines() {
        TranslationController translationController = TranslationController.builder().buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTranslationEngines();
        });

        assertEquals(CoreTranslationError.NO_TRANSLATION_ENGINES, contractException.getErrorType());

        // class to type map not populated
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            TranslationController translationController2 = TranslationController.builder()
                    .addTranslationEngine(TestTranslationEngine.builder().build()).buildWithoutInitAndChecks();
            for (TranslationEngine translationEngine : translationController2.data.translationEngines.values()) {
                translationEngine.init();
                translationController2.translationEngines.put(translationEngine.getTranslationEngineType(),
                        translationEngine);
            }
            translationController2.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        // class to type map not fully mapped
        runtimeException = assertThrows(RuntimeException.class, () -> {
            TranslationController translationController2 = TranslationController.builder()
                    .addTranslationEngine(TestTranslationEngine.builder().build()).buildWithoutInitAndChecks();
            for (TranslationEngine translationEngine : translationController2.data.translationEngines.values()) {
                translationEngine.init();
                translationController2.translationEngines.put(translationEngine.getTranslationEngineType(),
                        translationEngine);
                translationController2.translationEngineClassToTypeMap.put(translationEngine.getClass(), null);
            }
            translationController2.validateTranslationEngines();
        });

        assertEquals(
                "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getTranslationEngine", args = { Class.class })
    public void testGetTranslationEngine() {
        TestTranslationEngine expectedValue = TestTranslationEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec()).build();
        expectedValue.init();

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(expectedValue).build();

        assertEquals(expectedValue, translationController.getTranslationEngine(TestTranslationEngine.class));

        // precondition
        // translation engine is null and is tested by the test:
        // testValidateTranslationEngine()

        // classRef passed in does not match the class of the translation engine
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.getTranslationEngine(TranslationEngine.class);
        });

        assertEquals(CoreTranslationError.INVALID_TRANSLATION_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "readInput", args = {})
    public void testReadInput() {
        String fileName = "readInput-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        translationController.readInput();

        assertTrue(translationController.getObjects().size() == 1);

        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);

        assertEquals(expectedAppObject, actualTestAppObject);

        // preconditions

        // invalid file path
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.readInput(filePath.resolve("badPath"), TestInputObject.class,
                    TestTranslationEngine.builder().build());
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestForCoverage
    public void testMakeFileWriter() {
        String fileName = "MakeFileWriter-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build()).build();

        FileWriter actuaFileWriter = translationController.makeFileWriter(filePath.resolve(fileName));

        assertNotNull(actuaFileWriter);
        // preconditions

        // if the filePath is invalid
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.makeFileWriter(filePath);
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestForCoverage
    public void testGetOutputPath() {
        String fileName = "GetOutputPath_1-testOutput.json";
        String fileName2 = "GetOutputPath_2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);
        TestResourceHelper.createTestOutputFile(filePath, fileName2);

        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build())
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), Object.class, 1, TranslationEngineType.CUSTOM)
                .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

        Pair<Path, Optional<Class<TestAppObject>>> expectedPair1 = new Pair<>(filePath.resolve(fileName),
                Optional.empty());
        Pair<Path, Optional<Class<Object>>> expectedPair2 = new Pair<>(filePath.resolve(fileName2),
                Optional.of(Object.class));

        Pair<Path, Optional<Class<TestAppObject>>> actualPair1 = translationController
                .getOutputPath(TestAppObject.class, 0);
        Pair<Path, Optional<Class<Object>>> actualPair2 = translationController.getOutputPath(TestAppObject.class, 1);

        assertEquals(expectedPair1, actualPair1);
        assertEquals(expectedPair2, actualPair2);
        // preconditions

        // if the class scenarioId pair does not exist and there is no parent child
        // class relationship
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.getOutputPath(TestInputObject.class, 1);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_CLASSREF, contractException.getErrorType());

        // if the class and scenarioID pair does not exist AND there is a parent child
        // class relationship AND the parentClass scenarioId pair does not exists
        contractException = assertThrows(ContractException.class, () -> {
            translationController.getOutputPath(TestAppObject.class, 4);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { List.class })
    public void testWriteOutput_List() {
        String fileName = "WriteOutput_List_1-testOutput.json";
        String fileName2 = "WriteOutput_List_2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);
        TestResourceHelper.createTestOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestComplexAppObject.class,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<Object> outputObjects = new ArrayList<>();

        outputObjects.add(TestObjectUtil.generateTestAppObject());
        outputObjects.add(TestObjectUtil.generateTestComplexAppObject());
        translationController.writeOutput(outputObjects);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { List.class, Integer.class })
    public void testWriteOutput_List_ScenarioId() throws IOException {
        String fileName = "WriteOutput_List_ScenarioId_1-testOutput.json";
        String fileName2 = "WriteOutput_List_ScenarioId_2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);
        TestResourceHelper.createTestOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestComplexAppObject.class, 1,
                        TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<Object> outputObjects = new ArrayList<>();

        outputObjects.add(TestObjectUtil.generateTestAppObject());
        outputObjects.add(TestObjectUtil.generateTestComplexAppObject());
        translationController.writeOutput(outputObjects, 1);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class })
    public void testWriteOutput() throws IOException {
        String fileName = "writeOutput-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "writeOutput", args = { Object.class, Integer.class })
    public void testWriteOutput_ScenarioId() throws IOException {
        String fileName = "writeOutput_ScenarioId-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, 1);

        // preconditions
        // the runtime exception is covered by the test - testMakeFileWriter()
        // the contract exception for CoreTranslationError.INVALID_OUTPUT_CLASSREF is
        // covered by the test - testGetOutputPath()
        // the contract exception for CoreTranslationError.NULL_TRANSLATION_ENGINE is
        // covered by the test - testValidateTranslationEngine()
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getFirstObject", args = { Class.class })
    public void testGetFirstObject() throws IOException {
        String fileName = "getFirstObject-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject);

        translationController.readInput();

        assertTrue(translationController.getObjects().size() == 1);

        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);

        assertNotNull(actualTestAppObject);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {

            translationController.getFirstObject(TestInputObject.class);
        });

        assertEquals(CoreTranslationError.UNKNOWN_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getObjects", args = { Class.class })
    public void testGetObjects_OfClass() throws IOException {
        String fileName = "GetObjects_OfClass_1-testOutput.json";
        String fileName2 = "GetObjects_OfClass_2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);
        TestResourceHelper.createTestOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class, 2, TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), 1);
        translationController.writeOutput(expectedObjects.get(1), 2);

        translationController.readInput();

        assertEquals(2, translationController.getObjects().size());

        List<TestAppObject> actualObjects = translationController.getObjects(TestAppObject.class);

        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));

        List<TestComplexAppObject> actualObjects2 = translationController.getObjects(TestComplexAppObject.class);
        assertTrue(actualObjects2.isEmpty());
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "getObjects", args = {})
    public void testGetObjects() throws IOException {
        String fileName = "GetObjects_1-testOutput.json";
        String fileName2 = "GetObjects_2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);
        TestResourceHelper.createTestOutputFile(filePath, fileName2);

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TranslationController translationController = TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, 1, TranslationEngineType.CUSTOM)
                .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class, 2, TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(testTranslationEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), 1);
        translationController.writeOutput(expectedObjects.get(1), 2);

        translationController.readInput();

        assertEquals(2, translationController.getObjects().size());

        List<Object> actualObjects = translationController.getObjects();

        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));
    }

    @Test
    @UnitTestMethod(target = TranslationController.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(TranslationController.builder());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "build", args = {})
    public void testBuild() {
        TranslationController translationController = TranslationController.builder()
                .addTranslationEngine(TestTranslationEngine.builder().build()).build();

        assertNotNull(translationController);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().build();
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addInputFilePath", args = { Path.class,
            Class.class })
    public void testAddInputFilePath() {
        String fileName = "addInputFilePath-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        assertDoesNotThrow(() -> TranslationController.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(TestTranslationEngine.builder().build()).build());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(null, TestInputObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(filePath.resolve(fileName), null,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM)
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_INPUT_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addInputFilePath(filePath.resolve("badPath"), TestInputObject.class,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.INVALID_INPUT_PATH, contractException.getErrorType());

    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addOutputFilePath", args = { Path.class,
            Class.class })
    public void testAddOutputFilePath() {
        String fileName = "addOutputFilePath1-testOutput.json";
        String fileName2 = "addOutputFilePath2-testOutput.json";

        TestResourceHelper.createTestOutputFile(filePath, fileName);

        assertDoesNotThrow(() -> TranslationController.builder()
                .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                .addTranslationEngine(TestTranslationEngine.builder().build()).build());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addOutputFilePath(null, TestAppObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addOutputFilePath(filePath.resolve(fileName), null,
                    TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_OUTPUT_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder()
                    .addOutputFilePath(filePath.resolve(fileName), TestAppObject.class, TranslationEngineType.CUSTOM)
                    .addOutputFilePath(filePath.resolve(fileName2), TestAppObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF_SCENARIO_PAIR, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addOutputFilePath(filePath.resolve("badpath").resolve(fileName2),
                    TestAppObject.class, TranslationEngineType.CUSTOM);
        });

        assertEquals(CoreTranslationError.INVALID_OUTPUT_PATH, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addOutputFilePath", args = { Path.class,
            Class.class, Integer.class })
    public void testAddOutputFilePath_ScenarioId() {
        // Tested by testAddOutputFilePath, which internally calls
        // addOutputFilePath(path, class, 0)
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, Object.class);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addParentChildClassRelationship(null, Object.class);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, null);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addParentChildClassRelationship(TestAppObject.class, Object.class);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationController.Builder.class, name = "addTranslationEngineBuilder", args = {
            TranslationEngine.Builder.class })
    public void testAddTransationEngineBuilder() {
        TestTranslationEngine translationEngine = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

        TranslationController.builder().addTranslationEngine(translationEngine).build();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationController.builder().addTranslationEngine(null);
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_ENGINE, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TestTranslationEngine translationEngine2 = TestTranslationEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator())
                    .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

            TranslationController.builder().addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addTranslationEngine(translationEngine2);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }
}
