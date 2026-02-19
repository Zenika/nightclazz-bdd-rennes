package bplustree;

import bplustree.index.IndexEntry;
import bplustree.storage.PageManager;
import bplustree.valueobjects.Key;
import bplustree.valueobjects.Location;
import bplustree.valueobjects.Offset;
import bplustree.valueobjects.PageId;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {

    public enum IdStrategy { SEQUENTIAL, UUID_V4 }

    private final IdStrategy idStrategy;
    private final BPlusTree<IndexEntry> index;
    private final PageManager<Integer> pageManager;
    private final Map<Integer, Location> locationById;
    private final Map<Integer, MemoryAddress> addressById;
    private final ObjectMapper jsonMapper;
    private final AtomicInteger sequentialCounter;
    private final int treeOrder;
    private final MemoryAddressGenerator addressGenerator;

    private Database(int treeOrder, int pageSize, IdStrategy idStrategy) {
        this.treeOrder        = treeOrder;
        this.idStrategy       = idStrategy;
        this.index            = new BPlusTree<>(treeOrder);
        this.pageManager      = new PageManager<>(pageSize);
        this.locationById     = new LinkedHashMap<>();
        this.addressById      = new LinkedHashMap<>();
        this.jsonMapper       = new ObjectMapper();
        this.sequentialCounter = new AtomicInteger(1);
        this.addressGenerator = new MemoryAddressGenerator();
    }

    public static Database withSequentialIds(int treeOrder, int pageSize) {
        return new Database(treeOrder, pageSize, IdStrategy.SEQUENTIAL);
    }

    public static Database withUuidV4Ids(int treeOrder, int pageSize) {
        return new Database(treeOrder, pageSize, IdStrategy.UUID_V4);
    }

    public InsertResult insert(String jsonData) {
        assertValidJson(jsonData);

        int id = generateId();
        MemoryAddress memoryAddress = addressGenerator.generateRandom();
        Location location = pageManager.insert(id, jsonData);

        IndexEntry entry = new IndexEntry(new Key(id), location);
        index.insert(entry);
        locationById.put(id, location);
        addressById.put(id, memoryAddress);

        return new InsertResult(id, location, memoryAddress);
    }

    public SearchResult findById(int id) {
        IndexEntry probe = new IndexEntry(new Key(id), new Location(new PageId(0), new Offset(0)));
        boolean exists = index.search(probe);
        if (!exists) {
            return SearchResult.notFound(id);
        }
        Location location = locationById.get(id);
        String jsonData = pageManager.getJsonData(location);
        MemoryAddress memoryAddress = addressById.get(id);

        return SearchResult.found(id, jsonData, location, memoryAddress);
    }

    public void printTree() {
        System.out.println(TreePrinter.print(index.getRoot(), treeOrder));
    }

    public int treeOrder() {
        return treeOrder;
    }

    // -------------------------------------------------------------------------

    private int generateId() {
        return switch (idStrategy) {
            case SEQUENTIAL -> sequentialCounter.getAndIncrement();
            case UUID_V4    -> Math.abs(UUID.randomUUID().hashCode());
        };
    }

    private void assertValidJson(String json) {
        try {
            jsonMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON invalide : " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------

    public record InsertResult(int id, Location location, MemoryAddress memoryAddress) {
        @Override
        public String toString() {
            return "==> id " + id + "  [" + memoryAddress + "]";
        }
    }

    public record SearchResult(int id, boolean found, String jsonData, Location location, MemoryAddress memoryAddress) {

        static SearchResult found(int id, String jsonData, Location location, MemoryAddress memoryAddress) {
            return new SearchResult(id, true, jsonData, location, memoryAddress);
        }

        static SearchResult notFound(int id) {
            return new SearchResult(id, false, null, null, null);
        }

        @Override
        public String toString() {
            if (!found) return "  (aucun résultat)";
            return "  " + jsonData
                    + "\n  @ " + location
                    + "  [" + memoryAddress + "]";
        }
    }
}

