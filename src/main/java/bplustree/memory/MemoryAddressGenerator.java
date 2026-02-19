package bplustree.memory;

import java.util.Random;

public class MemoryAddressGenerator {

    private final Random random;

    public MemoryAddressGenerator() {
        this.random = new Random();
    }

    public MemoryAddress generateRandom() {
        int sector = random.nextInt(0xFFFF); // 16 bits pour le secteur
        int offset = random.nextInt(0xFFFF); // 16 bits pour l'offset
        return new MemoryAddress(sector, offset);
    }

}
