package bplustree.index;

import bplustree.database.Key;
import bplustree.database.Location;

/**
 * Représente une entrée dans l'index B+ Tree
 * Association d'une clé et de sa localisation physique
 */
public record IndexEntry(Key key, Location location) implements Comparable<IndexEntry> {

    @Override
    public int compareTo(IndexEntry other) {
        return this.key.compareTo(other.key);
    }

    @Override
    public String toString() {
        return "IndexEntry[key=" + key + ", location=" + location + "]";
    }
}
