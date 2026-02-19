package bplustree.database;

public record Key(Integer value) implements Comparable<Key> {

    @Override
    public int compareTo(Key other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return "RecordKey[" + value + "]";
    }
}
