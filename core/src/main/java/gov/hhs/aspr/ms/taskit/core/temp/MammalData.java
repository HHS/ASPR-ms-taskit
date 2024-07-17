package gov.hhs.aspr.ms.taskit.core.temp;

public final class MammalData {
    private final AnimalData animalData;
    private final Terrain terrain;
    private final double gestationPeriod;

    private MammalData(AnimalData animalData, Terrain terrain, double gestationPeriod) {
        this.animalData = animalData;
        this.terrain = terrain;
        this.gestationPeriod = gestationPeriod;
    }

    public static class Builder {
        private AnimalData.Builder animalDataBuilder = AnimalData.builder();
        private Terrain terrain;
        private double gestationPeriod;

        public MammalData build() {
            return new MammalData(animalDataBuilder.build(), terrain, gestationPeriod);
        }

        public Builder setTerrain(Terrain terrain) {
            this.terrain = terrain;

            return this;
        }

        public Builder setGestationPeriod(double gestationTime) {
            this.gestationPeriod = gestationTime;

            return this;
        }

        public Builder setAge(int age) {
            this.animalDataBuilder.setAge(age);

            return this;
        }

        public Builder setLifespan(double lifespan) {
            this.animalDataBuilder.setLifespan(lifespan);

            return this;
        }

        public Builder setScientificName(String scientificName) {
            this.animalDataBuilder.setScientificName(scientificName);

            return this;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public double getGestationPeriod() {
        return gestationPeriod;
    }

    public AnimalData getAnimalData() {
        return animalData;
    }

}
