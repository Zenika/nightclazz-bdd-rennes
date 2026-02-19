package bplustree.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    @Test
    void testRecordKeyCreation() {
        Key key = new Key(42);
        assertEquals(42, key.value());
    }

    @Test
    void testRecordKeyComparison() {
        Key key1 = new Key(10);
        Key key2 = new Key(20);
        Key key3 = new Key(10);

        assertTrue(key1.compareTo(key2) < 0);
        assertTrue(key2.compareTo(key1) > 0);
        assertEquals(0, key1.compareTo(key3));
    }

    @Test
    void testPageIdCreation() {
        PageId pageId = new PageId(5);
        assertEquals(5, pageId.value());
    }

    @Test
    void testPageIdNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PageId(null));
    }

    @Test
    void testPageIdNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PageId(-1));
    }

    @Test
    void testPageIdZeroIsValid() {
        PageId pageId = new PageId(0);
        assertEquals(0, pageId.value());
    }

    @Test
    void testOffsetCreation() {
        Offset offset = new Offset(100);
        assertEquals(100, offset.value());
    }

    @Test
    void testOffsetNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Offset(null));
    }

    @Test
    void testOffsetNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Offset(-1));
    }

    @Test
    void testOffsetZeroIsValid() {
        Offset offset = new Offset(0);
        assertEquals(0, offset.value());
    }

    @Test
    void testRecordLocationCreation() {
        PageId pageId = new PageId(3);
        Offset offset = new Offset(42);
        Location location = new Location(pageId, offset);

        assertEquals(pageId, location.pageId());
        assertEquals(offset, location.offset());
    }

    @Test
    void testRecordLocationEquality() {
        PageId pageId1 = new PageId(3);
        Offset offset1 = new Offset(42);
        Location location1 = new Location(pageId1, offset1);

        PageId pageId2 = new PageId(3);
        Offset offset2 = new Offset(42);
        Location location2 = new Location(pageId2, offset2);

        assertEquals(location1, location2);
    }

    @Test
    void testRecordLocationToString() {
        PageId pageId = new PageId(3);
        Offset offset = new Offset(42);
        Location location = new Location(pageId, offset);

        String str = location.toString();
        assertTrue(str.contains("3"));
        assertTrue(str.contains("42"));
    }
}
