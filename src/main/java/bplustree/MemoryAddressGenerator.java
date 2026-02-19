package bplustree;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryAddressGenerator {

    private final Random random;
    private final AtomicInteger sequentialSector;
    private final AtomicInteger sequentialOffset;

    public MemoryAddressGenerator() {
        this.random = new Random();
        this.sequentialSector = new AtomicInteger(0);
        this.sequentialOffset = new AtomicInteger(0);
    }

    public MemoryAddressGenerator(long seed) {
        this.random = new Random(seed);
        this.sequentialSector = new AtomicInteger(0);
        this.sequentialOffset = new AtomicInteger(0);
    }

    public MemoryAddress generateRandom() {
        int sector = random.nextInt(0xFFFF); // 16 bits pour le secteur
        int offset = random.nextInt(0xFFFF); // 16 bits pour l'offset
        return new MemoryAddress(sector, offset);
    }

    public MemoryAddress generateRandom(int maxSector, int maxOffset) {
        int sector = random.nextInt(maxSector + 1);
        int offset = random.nextInt(maxOffset + 1);
        return new MemoryAddress(sector, offset);
    }

    public MemoryAddress generateSequential() {
        int offset = sequentialOffset.getAndIncrement();
        int sector = sequentialSector.get();

        // Si l'offset dépasse 0xFFFF, on passe au secteur suivant
        if (offset > 0xFFFF) {
            sector = sequentialSector.incrementAndGet();
            offset = 0;
            sequentialOffset.set(1);
        }

        return new MemoryAddress(sector, offset);
    }

    public MemoryAddress generate(int sector, int offset) {
        return new MemoryAddress(sector, offset);
    }

    public void resetSequential() {
        sequentialSector.set(0);
        sequentialOffset.set(0);
    }

    public MemoryAddress fromValue(int value) {
        int sector = (value >> 16) & 0xFFFF;
        int offset = value & 0xFFFF;
        return new MemoryAddress(sector, offset);
    }
}
