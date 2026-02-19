package bplustree.storage.examples;

import bplustree.MemoryAddressGenerator;
import bplustree.storage.DataPage;
import bplustree.storage.PageManager;
import bplustree.valueobjects.Location;

public class PageStorageExamples {

    public static void main(String[] args) {
        exemple1_DataPageBasique();
        exemple3_InsertionsDesordonnees();
        exemple4_InsertionsOrdonnees();
    }

    /**
     * Exemple 1 : Fonctionnement de base d'une DataPage
     */
    private static void exemple1_DataPageBasique() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ EXEMPLE 1 : DataPage basique (taille 4)                │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        DataPage<Integer> page = new DataPage<>(4);
        MemoryAddressGenerator addrGen = new MemoryAddressGenerator();

        System.out.println("Insertions : 1, 2, 3, 4\n");
        page.insert(1, addrGen.generateSequential());
        page.insert(2, addrGen.generateSequential());
        page.insert(3, addrGen.generateSequential());
        page.insert(4, addrGen.generateSequential());

        System.out.println("Index triés       : " + page.getSortedIndexList());
        System.out.println("Valeurs triées    : " + page.getValuesSorted());
        System.out.println("Min: " + page.getMinValue() + ", Max: " + page.getMaxValue());
        System.out.println("Pleine: " + page.isFull());

        System.out.println("\n");
    }

    /**
     * Exemple 3 : Insertions désordonnées (UUID v4)
     */
    private static void exemple3_InsertionsDesordonnees() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ EXEMPLE 3 : Insertions désordonnées (UUID v4)          │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        PageManager<Integer> pm = new PageManager<>(4);

        int[] valeurs = {7, 10, 5, 9, 8, 3, 12, 1, 6};

        System.out.println("Séquence d'insertion : 7, 10, 5, 9, 8, 3, 12, 1, 6");
        System.out.println("(Simule des UUID v4 désordonnés)\n");

        for (int i = 0; i < valeurs.length; i++) {
            int valeur = valeurs[i];
            String jsonData = "{\"id\":" + (i + 1) + ",\"value\":" + valeur + "}";
            Location loc = pm.insert(valeur, jsonData);
            System.out.printf("  %d. insert(%2d) → PageId=%d, Offset=%d",
                    i + 1, valeur, loc.pageId().value(), loc.offset().value());

            if (i > 0 && pm.getPageCount() > (i > 4 ? 2 : 1)) {
                System.out.print("  ← SPLIT !");
            }
            System.out.println();
        }

        System.out.println("\nRÉSULTAT FINAL:");
        System.out.println("  Nombre de pages : " + pm.getPageCount());
        System.out.println("  Valeurs triées  : " + pm.getAllValuesSorted());

        System.out.println("\n  Détail des pages:");
        afficherPages(pm);

        System.out.println("\n");
    }

    /**
     * Exemple 4 : Insertions ordonnées (UUID v7)
     */
    private static void exemple4_InsertionsOrdonnees() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ EXEMPLE 4 : Insertions ordonnées (UUID v7)             │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        PageManager<Integer> pm = new PageManager<>(4);

        System.out.println("Séquence d'insertion : 1, 2, 3, 4, 5, 6, 7, 8, 9");
        System.out.println("(Simule des UUID v7 ordonnés)\n");

        int previousPageCount = 1;
        for (int i = 1; i <= 9; i++) {
            String jsonData = "{\"id\":" + i + ",\"value\":" + i + "}";
            Location loc = pm.insert(i, jsonData);
            System.out.printf("  %d. insert(%d) → PageId=%d, Offset=%d",
                    i, i, loc.pageId().value(), loc.offset().value());

            if (pm.getPageCount() > previousPageCount) {
                System.out.print("  ← SPLIT (mais déjà trié)");
                previousPageCount = pm.getPageCount();
            }
            System.out.println();
        }

        System.out.println("\nRÉSULTAT FINAL:");
        System.out.println("  Nombre de pages : " + pm.getPageCount());
        System.out.println("  Valeurs triées  : " + pm.getAllValuesSorted());

        System.out.println("\n  Détail des pages:");
        afficherPages(pm);

        System.out.println("\n");
    }

    /**
     * Utilitaire pour afficher les pages
     */
    private static void afficherPages(PageManager<Integer> pm) {
        pm.getAllPages().entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e1.getKey().value(), e2.getKey().value()))
                .forEach(entry ->
                    System.out.printf("    Page %d : %s (taille: %d/4)\n",
                            entry.getKey().value(),
                            entry.getValue().getValuesSorted(),
                            entry.getValue().activeSize())
                );
    }
}
