package bplustree.storage;

import bplustree.valueobjects.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageManagerTest {

    private PageManager<Integer> pageManager;

    @BeforeEach
    void setUp() {
        pageManager = new PageManager<>(4);
    }

    @Test
    void testInsertSingleValue() {
        Location location = pageManager.insert(42, "{\"id\":1,\"value\":42}");

        assertNotNull(location);
        assertEquals(0, location.pageId().value());
        assertEquals(0, location.offset().value());
        assertEquals(42, pageManager.getValue(location));
        assertEquals("{\"id\":1,\"value\":42}", pageManager.getJsonData(location));
    }

    @Test
    void testInsertMultipleValuesInSamePage() {
        Location loc1 = pageManager.insert(7, "{\"id\":1}");
        Location loc2 = pageManager.insert(10, "{\"id\":2}");
        Location loc3 = pageManager.insert(5, "{\"id\":3}");

        assertEquals(0, loc1.pageId().value());
        assertEquals(0, loc2.pageId().value());
        assertEquals(0, loc3.pageId().value());

        assertEquals(0, loc1.offset().value());
        assertEquals(1, loc2.offset().value());
        assertEquals(2, loc3.offset().value());

        assertEquals(7, pageManager.getValue(loc1));
        assertEquals(10, pageManager.getValue(loc2));
        assertEquals(5, pageManager.getValue(loc3));
    }

    @Test
    void testPageSplitWhenFull() {
        pageManager.insert(7, "{\"id\":1}");
        pageManager.insert(10, "{\"id\":2}");
        pageManager.insert(5, "{\"id\":3}");
        pageManager.insert(9, "{\"id\":4}");

        assertEquals(1, pageManager.getPageCount());

        Location loc5 = pageManager.insert(8, "{\"id\":5}");

        assertEquals(2, pageManager.getPageCount());
        assertNotNull(loc5);
        assertNotNull(pageManager.getValue(loc5));
    }

    @Test
    void testSplitPreservesAllValues() {
        pageManager.insert(7, "{\"id\":1}");
        pageManager.insert(10, "{\"id\":2}");
        pageManager.insert(5, "{\"id\":3}");
        pageManager.insert(9, "{\"id\":4}");
        pageManager.insert(8, "{\"id\":5}");

        List<Integer> allValues = pageManager.getAllValuesSorted();
        assertEquals(List.of(5, 7, 8, 9, 10), allValues);
    }

    @Test
    void testMultipleSplits() {
        for (int i = 10; i >= 1; i--) {
            pageManager.insert(i, "{\"id\":" + i + "}");
        }

        List<Integer> allValues = pageManager.getAllValuesSorted();
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), allValues);

        assertTrue(pageManager.getPageCount() >= 3);
    }

    @Test
    void testExampleScenarioFromUser() {
        pageManager.insert(7, "{\"id\":1}");
        pageManager.insert(10, "{\"id\":2}");
        pageManager.insert(5, "{\"id\":3}");
        pageManager.insert(9, "{\"id\":4}");
        pageManager.insert(8, "{\"id\":5}");

        List<Integer> allValues = pageManager.getAllValuesSorted();
        assertEquals(List.of(5, 7, 8, 9, 10), allValues);

        assertEquals(2, pageManager.getPageCount());
    }

    @Test
    void testSequentialInserts() {
        for (int i = 1; i <= 8; i++) {
            pageManager.insert(i, "{\"id\":" + i + "}");
        }

        List<Integer> allValues = pageManager.getAllValuesSorted();
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), allValues);

        assertTrue(pageManager.getPageCount() >= 2);
    }

    @Test
    void testInsertWithStrings() {
        PageManager<String> stringPageManager = new PageManager<>(3);

        stringPageManager.insert("banana", "{\"name\":\"banana\"}");
        stringPageManager.insert("apple", "{\"name\":\"apple\"}");
        stringPageManager.insert("cherry", "{\"name\":\"cherry\"}");
        stringPageManager.insert("date", "{\"name\":\"date\"}");

        List<String> allValues = stringPageManager.getAllValuesSorted();
        assertEquals(List.of("apple", "banana", "cherry", "date"), allValues);
    }
}


