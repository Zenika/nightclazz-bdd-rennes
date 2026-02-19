package bplustree.storage;

import bplustree.MemoryAddressGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataPageTest {

    private DataPage<Integer> page;
    private MemoryAddressGenerator addressGenerator;

    @BeforeEach
    void setUp() {
        page = new DataPage<>(4);
        addressGenerator = new MemoryAddressGenerator();
    }

    @Test
    void testInsertSingleValue() {
        int offset = page.insert(7, addressGenerator.generateSequential());

        assertEquals(0, offset);
        assertEquals(1, page.activeSize());
        assertEquals(7, page.getValue(0));
    }

    @Test
    void testInsertionOrderListAndSortedIndexList() {
        page.insert(7, addressGenerator.generateSequential());
        page.insert(10, addressGenerator.generateSequential());
        page.insert(5, addressGenerator.generateSequential());
        page.insert(9, addressGenerator.generateSequential());

        List<Integer> sortedIndexes = page.getSortedIndexList();
        assertEquals(List.of(2, 0, 3, 1), sortedIndexes);

        List<Integer> sortedValues = page.getValuesSorted();
        assertEquals(List.of(5, 7, 9, 10), sortedValues);
    }

    @Test
    void testPageIsFull() {
        assertFalse(page.isFull());

        page.insert(7, addressGenerator.generateSequential());
        assertFalse(page.isFull());

        page.insert(10, addressGenerator.generateSequential());
        assertFalse(page.isFull());

        page.insert(5, addressGenerator.generateSequential());
        assertFalse(page.isFull());

        page.insert(9, addressGenerator.generateSequential());
        assertTrue(page.isFull());
    }

    @Test
    void testCannotInsertWhenFull() {
        page.insert(7, addressGenerator.generateSequential());
        page.insert(10, addressGenerator.generateSequential());
        page.insert(5, addressGenerator.generateSequential());
        page.insert(9, addressGenerator.generateSequential());

        int offset = page.insert(8, addressGenerator.generateSequential());
        assertEquals(-1, offset);
        assertEquals(4, page.activeSize());
    }

    @Test
    void testGetMinAndMaxValue() {
        page.insert(7, addressGenerator.generateSequential());
        page.insert(10, addressGenerator.generateSequential());
        page.insert(5, addressGenerator.generateSequential());
        page.insert(9, addressGenerator.generateSequential());

        assertEquals(5, page.getMinValue());
        assertEquals(10, page.getMaxValue());
    }

    @Test
    void testSplitWithNewValue() {
        page.insert(7, addressGenerator.generateSequential());
        page.insert(10, addressGenerator.generateSequential());
        page.insert(5, addressGenerator.generateSequential());
        page.insert(9, addressGenerator.generateSequential());

        DataPage<Integer> newPage = new DataPage<>(4);
        int offset = page.splitAndCreateNewPage(8, addressGenerator.generateSequential(), newPage);

        assertEquals(0, offset);
        assertEquals(2, page.activeSize());
        assertEquals(List.of(5, 7), page.getValuesSorted());

        assertEquals(3, newPage.activeSize());
        assertEquals(List.of(8, 9, 10), newPage.getValuesSorted());
    }

    @Test
    void testInsertWithStrings() {
        DataPage<String> stringPage = new DataPage<>(3);
        MemoryAddressGenerator gen = new MemoryAddressGenerator();

        stringPage.insert("banana", gen.generateSequential());
        stringPage.insert("apple", gen.generateSequential());
        stringPage.insert("cherry", gen.generateSequential());

        List<String> sorted = stringPage.getValuesSorted();
        assertEquals(List.of("apple", "banana", "cherry"), sorted);
    }

}
