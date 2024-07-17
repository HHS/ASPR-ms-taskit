package gov.hhs.aspr.ms.taskit.core.temp;

import java.util.List;

public abstract class Dog extends Mammal {
    private final DogData dogData;

    public Dog(DogData dogData) {
        super(dogData.getMammalData());
        this.dogData = dogData;
    }

    public String getBreed() {
        return this.dogData.getBreed();
    }

    public List<String> getCharacteristics() {
        return this.dogData.getCharacteristics();
    }
}
