package bplustree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTest {

    @ParameterizedTest
    @BPlusTreeOrderTest(orders = {3, 4, 5})
    void testInsertAndSearch(int order) {
        BPlusTree<Integer> tree = new BPlusTree<>(order);

        tree.insert(10);
        tree.insert(20);
        tree.insert(5);

        assertTrue(tree.search(10));
        assertTrue(tree.search(20));
        assertTrue(tree.search(5));
        assertFalse(tree.search(15));
    }

    @ParameterizedTest
    @BPlusTreeOrderTest(orders = {3, 4, 5})
    void testSearchNotFound(int order) {
        BPlusTree<Integer> tree = new BPlusTree<>(order);

        tree.insert(10);
        tree.insert(20);

        assertFalse(tree.search(15));
        assertFalse(tree.search(100));
    }

    @ParameterizedTest
    @BPlusTreeOrderTest(orders = {3, 4, 5})
    void testSplitAndRebalance(int order) {
        BPlusTree<Integer> tree = new BPlusTree<>(order);

        // Insérer assez d'éléments pour forcer un split
        for (int i = 1; i <= order * 2; i++) {
            tree.insert(i * 10);
        }

        // Vérifier que la racine n'est plus une feuille après le split
        assertFalse(tree.getRoot().isLeaf());

        // Vérifier que tous les éléments sont toujours trouvables
        for (int i = 1; i <= order * 2; i++) {
            assertTrue(tree.search(i * 10), "Valeur " + (i * 10) + " devrait être trouvée");
        }
    }

    @Test
    void testInvalidOrder() {
        assertThrows(IllegalArgumentException.class, () -> new BPlusTree<>(2));
    }
}
