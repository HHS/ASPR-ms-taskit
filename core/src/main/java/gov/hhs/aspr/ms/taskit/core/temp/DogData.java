package gov.hhs.aspr.ms.taskit.core.temp;

import java.util.ArrayList;
import java.util.List;

public final class DogData {
    private final MammalData mammalData;
    private final String breed;
    private final List<String> characteristics;

    private DogData(MammalData mammalData, String breed, List<String> characteristics) {
        this.mammalData = mammalData;
        this.breed = breed;
        this.characteristics = new ArrayList<>(characteristics);
    }

    public static class Builder {
        private MammalData.Builder mammalBuilder = MammalData.builder();
        private String breed;
        private List<String> characteristics = new ArrayList<>();

        private Builder() {
        }

        public DogData build() {
            this.mammalBuilder.setTerrain(Terrain.LAND);

            return new DogData(mammalBuilder.build(), breed, characteristics);
        }

        public Builder setBreed(String breed) {
            this.breed = breed;

            return this;
        }

        public Builder addCharacteristic(String characteristic) {
            this.characteristics.add(characteristic);

            return this;
        }

        public Builder setGestationPeriod(double gestationTime) {
            this.mammalBuilder.setGestationPeriod(gestationTime);

            return this;
        }

        public Builder setAge(int age) {
            this.mammalBuilder.setAge(age);

            return this;
        }

        public Builder setLifespan(double lifespan) {
            this.mammalBuilder.setLifespan(lifespan);

            return this;
        }

        public Builder setScientificName(String scientificName) {
            this.mammalBuilder.setScientificName(scientificName);

            return this;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public MammalData getMammalData() {
        return mammalData;
    }

    public String getBreed() {
        return breed;
    }

    public List<String> getCharacteristics() {
        return characteristics;
    }

}
