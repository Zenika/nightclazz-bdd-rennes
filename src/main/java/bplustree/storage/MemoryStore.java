package bplustree.storage;

import bplustree.MemoryAddress;

import java.util.HashMap;
import java.util.Map;

public class MemoryStore {

    private final Map<MemoryAddress, String> storage;

    public MemoryStore() {
        this.storage = new HashMap<>();
    }

    public void store(MemoryAddress address, String jsonData) {
        storage.put(address, jsonData);
    }

    public String retrieve(MemoryAddress address) {
        return storage.get(address);
    }

    public boolean contains(MemoryAddress address) {
        return storage.containsKey(address);
    }

    public int size() {
        return storage.size();
    }
}
