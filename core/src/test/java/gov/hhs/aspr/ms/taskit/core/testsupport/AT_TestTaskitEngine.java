package gov.hhs.aspr.ms.taskit.core.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.TaskitEngineTestHelper;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineType;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.translationSpecs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.translationSpecs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TestTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "write", args = { Path.class, Object.class,
            Optional.class })
    public void testWrite() throws IOException {
        String fileName = "writeFromEngine_1-testOutput.json";
        String fileName2 = "writeFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.write(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = testTaskitEngine.read(filePath.resolve(fileName), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        testTaskitEngine.write(filePath.resolve(fileName2), TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = testTaskitEngine.read(filePath.resolve(fileName2), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "read", args = { Path.class, Class.class })
    public void testRead() throws IOException {
        String fileName = "readFromEngine_1-testOutput.json";
        String fileName2 = "readFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.write(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = testTaskitEngine.read(filePath.resolve(fileName), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        testTaskitEngine.write(filePath.resolve(fileName2), TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = testTaskitEngine.read(filePath.resolve(fileName2), TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(TestTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "build", args = {})
    public void testBuild() {
        assertNotNull(TestTaskitEngine.builder().build());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "buildWithUnknownType", args = {})
    public void testBuildWithUnknownType() {
        assertNotNull(TestTaskitEngine.builder().buildWithUnknownType());

        assertEquals(TaskitEngineType.UNKNOWN, TestTaskitEngine.builder().buildWithUnknownType().getTaskitEngineType());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "buildWithoutSpecInit", args = {})
    public void testBuildWithoutSpecInit() {
        assertNotNull(TestTaskitEngine.builder().buildWithoutSpecInit());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        TaskitEngineTestHelper.testAddTranslationSpec(TestTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        TaskitEngineTestHelper.testAddTranslator(TestTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        TaskitEngineTestHelper.testAddParentChildClassRelationship(TestTaskitEngine.builder());
    }
    
    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        // covered by AT_TaskitEngine#testHashCode
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        // covered by AT_TaskitEngine#testEquals
    }
}
