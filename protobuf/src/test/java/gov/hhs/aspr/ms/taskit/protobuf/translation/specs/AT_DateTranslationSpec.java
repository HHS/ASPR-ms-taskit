package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.google.type.Date;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_DateTranslationSpec {

    @Test
    @UnitTestConstructor(target = DateTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new DateTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        DateTranslationSpec dateTranslationSpec = new DateTranslationSpec();
        dateTranslationSpec.init(protobufTaskitEngine);

        LocalDate expectedValue = LocalDate.now();
        Date inputValue = Date.newBuilder().setDay(expectedValue.getDayOfMonth())
                .setMonth(expectedValue.getMonthValue()).setYear(expectedValue.getYear()).build();

        LocalDate actualValue = dateTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        DateTranslationSpec dateTranslationSpec = new DateTranslationSpec();
        dateTranslationSpec.init(protobufTaskitEngine);

        LocalDate appValue = LocalDate.now();
        Date inputValue = Date.newBuilder().setDay(appValue.getDayOfMonth()).setMonth(appValue.getMonthValue())
                .setYear(appValue.getYear()).build();

        Date actualValue = dateTranslationSpec.translateAppObject(appValue);

        assertEquals(inputValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = DateTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        DateTranslationSpec dateTranslationSpec = new DateTranslationSpec();

        assertEquals(LocalDate.class, dateTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = DateTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        DateTranslationSpec dateTranslationSpec = new DateTranslationSpec();

        assertEquals(Date.class, dateTranslationSpec.getInputObjectClass());
    }
}
