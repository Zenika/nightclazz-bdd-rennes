package bplustree.index;

import bplustree.BPlusTree;
import bplustree.valueobjects.Key;
import bplustree.valueobjects.Location;
import bplustree.valueobjects.PageId;
import bplustree.valueobjects.Offset;

import java.util.concurrent.atomic.AtomicInteger;

public class IndexManager {

    private final BPlusTree<IndexEntry> tree;
    private final AtomicInteger keyGenerator;
    private final AtomicInteger pageIdGenerator;
    private final AtomicInteger offsetGenerator;

    public IndexManager(int order) {
        this.tree = new BPlusTree<>(order);
        this.keyGenerator = new AtomicInteger(1);
        this.pageIdGenerator = new AtomicInteger(0);
        this.offsetGenerator = new AtomicInteger(0);
    }

    public Location insertData(Object data) {
        // Générer une nouvelle clé
        Key key = new Key(keyGenerator.getAndIncrement());

        // Générer une localisation physique
        Location location = generateLocation();

        // Créer l'entrée d'index
        IndexEntry entry = new IndexEntry(key, location);

        // Insérer dans l'arbre
        tree.insert(entry);

        return location;
    }


    public Location findByKey(Key key) {
        // Pour le moment, on utilise la méthode search qui retourne un booléen
        // Dans une implémentation complète, on devrait retourner l'entrée complète
        IndexEntry searchEntry = new IndexEntry(key, new Location(new PageId(0), new Offset(0)));
        boolean found = tree.search(searchEntry);

        // TODO: Retourner la vraie localisation quand on aura une méthode de recherche appropriée
        return found ? new Location(new PageId(0), new Offset(0)) : null;
    }

    private Location generateLocation() {
        int offset = offsetGenerator.getAndIncrement();
        int pageId = pageIdGenerator.get();

        // Simuler un changement de page après 100 enregistrements
        if (offset >= 100) {
            pageId = pageIdGenerator.incrementAndGet();
            offsetGenerator.set(0);
            offset = 0;
        }

        return new Location(new PageId(pageId), new Offset(offset));
    }

    public BPlusTree<IndexEntry> getTree() {
        return tree;
    }

    public void reset() {
        keyGenerator.set(1);
        pageIdGenerator.set(0);
        offsetGenerator.set(0);
    }
}
