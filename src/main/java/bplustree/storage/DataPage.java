package bplustree.storage;

import bplustree.memory.MemoryAddress;

import java.util.*;

public class DataPage<T extends Comparable<T>> {

    private final int pageSize;
    private final List<PageEntry<T>> insertionOrderList;
    private final List<Integer> sortedIndexList;
    private boolean hasSplit;

    public DataPage(int pageSize) {
        this.pageSize = pageSize;
        this.insertionOrderList = new ArrayList<>(pageSize);
        this.sortedIndexList = new ArrayList<>(pageSize);
        this.hasSplit = false;
    }

    public boolean isFull() {
        return sortedIndexList.size() >= pageSize || hasSplit;
    }

    private boolean isActuallyFull() {
        return sortedIndexList.size() >= pageSize;
    }

    public int activeSize() {
        return sortedIndexList.size();
    }

    public int insert(T value, MemoryAddress memoryAddress) {
        if (isFull()) {
            return -1;
        }

        PageEntry<T> entry = new PageEntry<>(value, memoryAddress);
        int offset = insertionOrderList.size();
        insertionOrderList.add(entry);

        int sortedPosition = findSortedPosition(value);
        sortedIndexList.add(sortedPosition, offset);

        return offset;
    }

    private int findSortedPosition(T value) {
        for (int i = 0; i < sortedIndexList.size(); i++) {
            int currentIndex = sortedIndexList.get(i);
            T currentValue = insertionOrderList.get(currentIndex).getValue();
            if (value.compareTo(currentValue) < 0) {
                return i;
            }
        }
        return sortedIndexList.size();
    }

    public PageEntry<T> getEntry(int offset) {
        if (offset < 0 || offset >= insertionOrderList.size()) {
            return null;
        }
        return insertionOrderList.get(offset);
    }

    public List<T> getValuesSorted() {
        List<T> sorted = new ArrayList<>(sortedIndexList.size());
        for (Integer index : sortedIndexList) {
            sorted.add(insertionOrderList.get(index).getValue());
        }
        return sorted;
    }

    public int splitAndCreateNewPage(T newValue, MemoryAddress newMemoryAddress, DataPage<T> newPage) {
        this.hasSplit = true;

        int insertPosition = -1;
        for (int i = 0; i < sortedIndexList.size(); i++) {
            int idx = sortedIndexList.get(i);
            T val = insertionOrderList.get(idx).getValue();
            if (newValue.compareTo(val) < 0) {
                insertPosition = i;
                break;
            }
        }

        if (insertPosition == -1) {
            insertPosition = sortedIndexList.size();
        }

        int newValueOffset = newPage.insert(newValue, newMemoryAddress);

        int copiedCount = 0;
        for (int i = insertPosition; i < sortedIndexList.size() && !newPage.isActuallyFull(); i++) {
            int idx = sortedIndexList.get(i);
            PageEntry<T> entry = insertionOrderList.get(idx);
            int result = newPage.insert(entry.getValue(), entry.getMemoryAddress());
            if (result >= 0) {
                copiedCount++;
            }
        }

        sortedIndexList.subList(insertPosition, insertPosition + copiedCount).clear();

        return newValueOffset;
    }

    @Override
    public String toString() {
        return "DataPage{" +
                "activeSize=" + activeSize() +
                ", sorted=" + getValuesSorted() +
                '}';
    }
}


