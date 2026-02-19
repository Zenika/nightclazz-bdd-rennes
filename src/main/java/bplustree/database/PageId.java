package bplustree.database;

/**
 * Value object représentant l'identifiant d'une page en mémoire
 */
public record PageId(Integer value) {

    @Override
    public String toString() {
        return "PageId[" + value + "]";
    }
}
