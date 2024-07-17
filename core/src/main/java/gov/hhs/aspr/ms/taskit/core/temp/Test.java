package gov.hhs.aspr.ms.taskit.core.temp;

public class Test {
    public static void main(String[] args) {
        AustralianShepard australianShepard = AustralianShepard.builder()
                .addCharacteristic("blue eye")
                .addCharacteristic("brown eye")
                .addCharacteristic("blue merle")
                .addCharacteristic("heart birthmark")
                .setDifferentColoredEyes(true)
                .setType(AnimalType.MINI)
                .setAge(5)
                .build();

        
    }
}
