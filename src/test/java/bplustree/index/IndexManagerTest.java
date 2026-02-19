package bplustree.index;

import bplustree.valueobjects.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexManagerTest {

    private IndexManager indexManager;

    @BeforeEach
    void setUp() {
        indexManager = new IndexManager(4);
    }

    @AfterEach
    void cleanUp() {
        indexManager.reset();
    }

    @Test
    void testInsertDataGeneratesLocation() {
        Location location = indexManager.insertData("Test data");

        assertNotNull(location);
        assertNotNull(location.pageId());
        assertNotNull(location.offset());
    }

    @Test
    void testInsertDataGeneratesSequentialLocations() {
        Location location1 = indexManager.insertData("Data 1");
        Location location2 = indexManager.insertData("Data 2");
        Location location3 = indexManager.insertData("Data 3");

        // Les premières locations devraient être sur la même page
        assertEquals(location1.pageId(), location2.pageId());
        assertEquals(location2.pageId(), location3.pageId());

        // Mais avec des offsets différents
        assertEquals(0, location1.offset().value());
        assertEquals(1, location2.offset().value());
        assertEquals(2, location3.offset().value());
    }

    @Test
    void testPageChangeAfter100Records() {
        Location firstLocation = indexManager.insertData("Data 0");
        assertEquals(0, firstLocation.pageId().value());
        assertEquals(0, firstLocation.offset().value());

        // Insérer 99 enregistrements supplémentaires
        for (int i = 1; i < 100; i++) {
            indexManager.insertData("Data " + i);
        }

        // Le 101ème enregistrement devrait être sur une nouvelle page
        Location location101 = indexManager.insertData("Data 100");
        assertEquals(1, location101.pageId().value());
        assertEquals(0, location101.offset().value());
    }

    @Test
    void testInsertManyRecordsToTriggerTreeRebalancing() {
        // Insérer suffisamment d'enregistrements pour forcer un rééquilibrage de l'arbre
        for (int i = 0; i < 20; i++) {
            Location location = indexManager.insertData("Data " + i);
            assertNotNull(location);
        }

        // Vérifier que l'arbre s'est bien construit
        assertNotNull(indexManager.getTree().getRoot());
    }
}
