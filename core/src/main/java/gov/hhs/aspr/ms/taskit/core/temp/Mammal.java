package gov.hhs.aspr.ms.taskit.core.temp;

public abstract class Mammal extends Animal {
    private final MammalData mammalData;

    public Mammal(MammalData mammalData) {
        super(mammalData.getAnimalData());
        this.mammalData = mammalData;
    }

    public Terrain getTerrain() {
        return mammalData.getTerrain();
    }

    public double getGestationPeriod() {
        return this.mammalData.getGestationPeriod();
    }

}