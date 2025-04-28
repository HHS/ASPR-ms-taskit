package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.random.RandomGenerator;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;

public enum TestClassPair {
    T1_T2(T1.class, T2.class), 
    T1_T3(T1.class, T3.class),
    T1_T4(T1.class, T4.class),
    T1_T5(T1.class, T5.class),
    T1_T6(T1.class, T6.class),
    T1_T7(T1.class, T7.class),
    T1_T8(T1.class, T8.class),
    T1_T9(T1.class, T9.class),
    T1_T10(T1.class, T10.class),
    T1_T11(T1.class, T11.class),
    T1_T12(T1.class, T12.class),
    T1_T13(T1.class, T13.class),
    T1_T14(T1.class, T14.class),
    T1_T15(T1.class, T15.class),
    T2_T3(T2.class, T3.class),
    T2_T4(T2.class, T4.class),
    T2_T5(T2.class, T5.class),
    T2_T6(T2.class, T6.class),
    T2_T7(T2.class, T7.class),
    T2_T8(T2.class, T8.class),
    T2_T9(T2.class, T9.class),
    T2_T10(T2.class, T10.class),
    T2_T11(T2.class, T11.class),
    T2_T12(T2.class, T12.class),
    T2_T13(T2.class, T13.class),
    T2_T14(T2.class, T14.class),
    T2_T15(T2.class, T15.class),
    T3_T4(T3.class, T4.class),
    T3_T5(T3.class, T5.class),
    T3_T6(T3.class, T6.class),
    T3_T7(T3.class, T7.class),
    T3_T8(T3.class, T8.class),
    T3_T9(T3.class, T9.class),
    T3_T10(T3.class, T10.class),
    T3_T11(T3.class, T11.class),
    T3_T12(T3.class, T12.class),
    T3_T13(T3.class, T13.class),
    T3_T14(T3.class, T14.class),
    T3_T15(T3.class, T15.class),
    T4_T5(T4.class, T5.class),
    T4_T6(T4.class, T6.class),
    T4_T7(T4.class, T7.class),
    T4_T8(T4.class, T8.class),
    T4_T9(T4.class, T9.class),
    T4_T10(T4.class, T10.class),
    T4_T11(T4.class, T11.class),
    T4_T12(T4.class, T12.class),
    T4_T13(T4.class, T13.class),
    T4_T14(T4.class, T14.class),
    T4_T15(T4.class, T15.class),
    T5_T6(T5.class, T6.class),
    T5_T7(T5.class, T7.class),
    T5_T8(T5.class, T8.class),
    T5_T9(T5.class, T9.class),
    T5_T10(T5.class, T10.class),
    T5_T11(T5.class, T11.class),
    T5_T12(T5.class, T12.class),
    T5_T13(T5.class, T13.class),
    T5_T14(T5.class, T14.class),
    T5_T15(T5.class, T15.class),
    T6_T7(T6.class, T7.class),
    T6_T8(T6.class, T8.class),
    T6_T9(T6.class, T9.class),
    T6_T10(T6.class, T10.class),
    T6_T11(T6.class, T11.class),
    T6_T12(T6.class, T12.class),
    T6_T13(T6.class, T13.class),
    T6_T14(T6.class, T14.class),
    T6_T15(T6.class, T15.class),
    T7_T8(T7.class, T8.class),
    T7_T9(T7.class, T9.class),
    T7_T10(T7.class, T10.class),
    T7_T11(T7.class, T11.class),
    T7_T12(T7.class, T12.class),
    T7_T13(T7.class, T13.class),
    T7_T14(T7.class, T14.class),
    T7_T15(T7.class, T15.class),
    T8_T9(T8.class, T9.class),
    T8_T10(T8.class, T10.class),
    T8_T11(T8.class, T11.class),
    T8_T12(T8.class, T12.class),
    T8_T13(T8.class, T13.class),
    T8_T14(T8.class, T14.class),
    T8_T15(T8.class, T15.class),
    T9_T10(T9.class, T10.class),
    T9_T11(T9.class, T11.class),
    T9_T12(T9.class, T12.class),
    T9_T13(T9.class, T13.class),
    T9_T14(T9.class, T14.class),
    T9_T15(T9.class, T15.class),
    T10_T11(T10.class, T11.class),
    T10_T12(T10.class, T12.class),
    T10_T13(T10.class, T13.class),
    T10_T14(T10.class, T14.class),
    T10_T15(T10.class, T15.class),
    T11_T12(T11.class, T12.class),
    T11_T13(T11.class, T13.class),
    T11_T14(T11.class, T14.class),
    T11_T15(T11.class, T15.class),
    T12_T13(T12.class, T13.class);

    private final Class<?> typeI;
    private final Class<?> typeA;
    
    private <I, A> TestClassPair(Class<I> typeI, Class<A> typeA) {
        this.typeI = typeI;
        this.typeA = typeA;
    }

    public Class<?> getTypeI() {
        return typeI;
    }

    public Class<?> getTypeA() {
        return typeA;
    }

    public static TestClassPair getRandomTestClassPair(final RandomGenerator randomGenerator) {
        return TestClassPair.values()[randomGenerator.nextInt(TestClassPair.values().length)];
    }

    public static List<TestClassPair> getTestClassPairs() {
        return Arrays.asList(TestClassPair.values());
    }

    public static List<TestClassPair> getShuffledTestClassPairs(final RandomGenerator randomGenerator) {
        List<TestClassPair> result = getTestClassPairs();
        Random random = new Random(randomGenerator.nextLong());
		Collections.shuffle(result, random);
		return result;
    }

    public TranslationSpec<?, ?, TestTaskitEngine> createTranslationSpec() {
        return new DynamicTranslationSpec<>(typeI, typeA);
    }

    private static final class DynamicTranslationSpec<I, A> extends TranslationSpec<I, A, TestTaskitEngine> {
        private final Class<I> typeI;
        private final Class<A> typeA;

        private DynamicTranslationSpec(Class<I> typeI, Class<A> typeA) {
            super(TestTaskitEngine.class);
            this.typeI = typeI;
            this.typeA = typeA;
        }

        @Override
        protected A translateInputObject(I inputObject) {
            throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
        }

        @Override
        protected I translateAppObject(A appObject) {
            throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
        }

        @Override
        public Class<A> getAppObjectClass() {
            return typeA;
        }

        @Override
        public Class<I> getInputObjectClass() {
            return typeI;
        }
    }

    private static class T1 {}
    private static class T2 {}
    private static class T3 {}
    private static class T4 {}
    private static class T5 {}
    private static class T6 {}
    private static class T7 {}
    private static class T8 {}
    private static class T9 {}
    private static class T10 {}
    private static class T11 {}
    private static class T12 {}
    private static class T13 {}
    private static class T14 {}
    private static class T15 {}
}
