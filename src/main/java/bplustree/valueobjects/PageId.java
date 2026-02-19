package bplustree.valueobjects;

/**
 * Value object représentant l'identifiant d'une page en mémoire
 */
public record PageId(Integer value) {

    public PageId {
        if (value == null) {
            throw new IllegalArgumentException("Le PageId ne peut pas être null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Le PageId doit être positif ou nul");
        }
    }

    @Override
    public String toString() {
        return "PageId[" + value + "]";
    }
}
