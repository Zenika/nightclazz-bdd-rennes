package bplustree.memory;

/**
 * Représente une adresse mémoire physique sur disque.
 * Format: Secteur:Offset (ex: 0x00A3:0x0F2B)
 */
public record MemoryAddress(int sector, int offset) implements Comparable<MemoryAddress> {

    @Override
    public String toString() {
        return String.format("0x%04X:0x%04X", sector, offset);
    }

    @Override
    public int compareTo(MemoryAddress other) {
        int sectorComparison = Integer.compare(this.sector, other.sector);
        if (sectorComparison != 0) {
            return sectorComparison;
        }
        return Integer.compare(this.offset, other.offset);
    }

}
