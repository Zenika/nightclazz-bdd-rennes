package bplustree.database;

/**
 * Value object représentant un offset dans une page
 */
public record Offset(Integer value) {

    @Override
    public String toString() {
        return "Offset[" + value + "]";
    }
}
