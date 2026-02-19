package bplustree.storage;

import bplustree.memory.MemoryAddress;
import bplustree.memory.MemoryAddressGenerator;
import bplustree.database.Location;
import bplustree.database.Offset;
import bplustree.database.PageId;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PageManager<T extends Comparable<T>> {

    private final int pageSize;
    private final Map<PageId, DataPage<T>> pages;
    private final AtomicInteger nextPageId;
    private final MemoryStore memoryStore;
    private final MemoryAddressGenerator addressGenerator;

    public PageManager(int pageSize) {
        this.pageSize = pageSize;
        this.pages = new HashMap<>();
        this.nextPageId = new AtomicInteger(0);
        this.memoryStore = new MemoryStore();
        this.addressGenerator = new MemoryAddressGenerator();
        pages.put(new PageId(0), new DataPage<>(pageSize));
    }

    public Location insert(T value, String jsonData) {
        MemoryAddress memoryAddress = addressGenerator.generateRandom();
        memoryStore.store(memoryAddress, jsonData);

        PageId targetPageId = findPageForValue();
        DataPage<T> page = pages.get(targetPageId);

        if (page.isFull()) {
            PageId newPageId = new PageId(nextPageId.incrementAndGet());
            DataPage<T> newPage = new DataPage<>(pageSize);
            int offset = page.splitAndCreateNewPage(value, memoryAddress, newPage);
            pages.put(newPageId, newPage);
            return new Location(newPageId, new Offset(offset));
        }

        int offset = page.insert(value, memoryAddress);
        return new Location(targetPageId, new Offset(offset));
    }

    private PageId findPageForValue() {
        for (int i = nextPageId.get(); i >= 0; i--) {
            PageId pageId = new PageId(i);
            DataPage<T> page = pages.get(pageId);
            if (page != null && !page.isFull()) {
                return pageId;
            }
        }
        return new PageId(nextPageId.get());
    }

    public String getJsonData(Location location) {
        DataPage<T> page = pages.get(location.pageId());
        if (page == null) {
            return null;
        }
        PageEntry<T> entry = page.getEntry(location.offset().value());
        if (entry == null) {
            return null;
        }
        return memoryStore.retrieve(entry.getMemoryAddress());
    }

    public int getPageCount() {
        return pages.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PageManager{\n");
        sb.append("  pageSize=").append(pageSize).append("\n");
        sb.append("  pageCount=").append(getPageCount()).append("\n");
        for (int i = 0; i <= nextPageId.get(); i++) {
            PageId pageId = new PageId(i);
            DataPage<T> page = pages.get(pageId);
            if (page != null) {
                sb.append("  Page ").append(i).append(": ").append(page).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
