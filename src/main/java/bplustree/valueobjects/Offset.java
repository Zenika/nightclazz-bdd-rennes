package bplustree.valueobjects;

/**
 * Value object représentant un offset dans une page
 */
public record Offset(Integer value) {

    public Offset {
        if (value == null) {
            throw new IllegalArgumentException("L'offset ne peut pas être null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("L'offset doit être positif ou nul");
        }
    }

    @Override
    public String toString() {
        return "Offset[" + value + "]";
    }
}
