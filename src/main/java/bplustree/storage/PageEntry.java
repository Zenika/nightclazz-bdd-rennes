package bplustree.storage;

import bplustree.MemoryAddress;

public class PageEntry<T extends Comparable<T>> {

    private final T value;
    private final MemoryAddress memoryAddress;

    public PageEntry(T value, MemoryAddress memoryAddress) {
        this.value = value;
        this.memoryAddress = memoryAddress;
    }

    public T getValue() {
        return value;
    }

    public MemoryAddress getMemoryAddress() {
        return memoryAddress;
    }

    @Override
    public String toString() {
        return "PageEntry{value=" + value + ", addr=" + memoryAddress + "}";
    }
}
