package gov.hhs.aspr.ms.taskit.core.temp;

public final class AustralianShepard extends Dog {

    private final Data data;

    private AustralianShepard(Data data, DogData dogData) {
        super(dogData);
        this.data = data;
    }

    private static final class Data {

        private boolean differentColoredEyes = false;
        private AnimalType type = AnimalType.NORMAL;
    }

    public static class Builder {
        private Data data;
        private DogData.Builder dogDataBuilder = DogData.builder();

        private Builder(Data data) {
            this.data = data;
        }

        public AustralianShepard build() {
            this.dogDataBuilder.addCharacteristic("Merle");
            this.dogDataBuilder.setBreed(AustralianShepard.class.getSimpleName());
            this.dogDataBuilder.setLifespan(14);
            this.dogDataBuilder.setGestationPeriod(16);
            this.dogDataBuilder.setScientificName("HerdingDog");

            return new AustralianShepard(this.data, this.dogDataBuilder.build());
        }

        public Builder addCharacteristic(String characteristic) {
            this.dogDataBuilder.addCharacteristic(characteristic);

            return this;
        }

        public Builder setAge(int age) {
            this.dogDataBuilder.setAge(age);

            return this;
        }

        public Builder setDifferentColoredEyes(boolean differentColoredEyes) {
            this.data.differentColoredEyes = differentColoredEyes;

            return this;
        }

        public Builder setType(AnimalType animalType) {
            this.data.type = animalType;

            return this;
        }
    }

    public static Builder builder() {
        return new Builder(new Data());
    }

    public boolean hasDifferentColoredEyes() {
        return this.data.differentColoredEyes;
    }

    public AnimalType getAnimalType() {
        return this.data.type;
    }

}
