package gov.hhs.aspr.ms.taskit.core.temp;

public final class AnimalData {
    private final int age;
    private final double lifespan;
    private final String scientificName;

    private AnimalData(int age, double lifespan, String scientificName) {
        this.age = age;
        this.lifespan = lifespan;
        this.scientificName = scientificName;
    }

    public static class Builder {
        private int age;
        private double lifespan;
        private String scientificName;

        private Builder() {
        }

        public AnimalData build() {
            return new AnimalData(age, lifespan, scientificName);
        }

        public Builder setAge(int age) {
            this.age = age;

            return this;
        }

        public Builder setLifespan(double lifespan) {
            this.lifespan = lifespan;

            return this;
        }

        public Builder setScientificName(String scientificName) {
            this.scientificName = scientificName;

            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getAge() {
        return age;
    }

    public double getLifespan() {
        return lifespan;
    }

    public String getScientificName() {
        return scientificName;
    }

}
