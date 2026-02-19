package bplustree.index;

import bplustree.valueobjects.Key;
import bplustree.valueobjects.Location;

/**
 * Représente une entrée dans l'index B+ Tree
 * Association d'une clé et de sa localisation physique
 */
public record IndexEntry(Key key, Location location) implements Comparable<IndexEntry> {

    public IndexEntry {
        if (key == null) {
            throw new IllegalArgumentException("La clé ne peut pas être null");
        }
        if (location == null) {
            throw new IllegalArgumentException("La location ne peut pas être null");
        }
    }

    @Override
    public int compareTo(IndexEntry other) {
        return this.key.compareTo(other.key);
    }

    @Override
    public String toString() {
        return "IndexEntry[key=" + key + ", location=" + location + "]";
    }
}
