package bplustree;

/**
 * Représente une adresse mémoire physique sur disque.
 * Format: Secteur:Offset (ex: 0x00A3:0x0F2B)
 */
public record MemoryAddress(int sector, int offset) implements Comparable<MemoryAddress> {

    public MemoryAddress {
        if (sector < 0) {
            throw new IllegalArgumentException("Le secteur ne peut pas être négatif");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("L'offset ne peut pas être négatif");
        }
    }

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

    /**
     * Parse une adresse mémoire depuis une chaîne de caractères
     * @param address Format: "0x00A3:0x0F2B"
     * @return L'adresse mémoire correspondante
     */
    public static MemoryAddress parse(String address) {
        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Format d'adresse invalide: " + address);
        }

        int sector = Integer.parseInt(parts[0].replace("0x", ""), 16);
        int offset = Integer.parseInt(parts[1].replace("0x", ""), 16);

        return new MemoryAddress(sector, offset);
    }
}
