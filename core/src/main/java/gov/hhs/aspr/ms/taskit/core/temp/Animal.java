package gov.hhs.aspr.ms.taskit.core.temp;

public abstract class Animal {
    private final AnimalData animalData;

    public Animal(AnimalData animalData) {
        this.animalData = animalData;
    }

    public int getAge() {
        return animalData.getAge();
    }

    public double getLifespan() {
        return animalData.getLifespan();
    }

    public String getScientificName() {
        return animalData.getScientificName();
    }

}
