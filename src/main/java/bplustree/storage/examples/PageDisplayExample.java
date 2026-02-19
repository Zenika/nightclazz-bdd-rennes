package bplustree.storage.examples;

import bplustree.storage.PageManager;

public class PageDisplayExample {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  EXEMPLE D'AFFICHAGE DÉTAILLÉ DES PAGES");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        PageManager<Integer> pm = new PageManager<>(4);

        System.out.println("Insertion de 4 valeurs dans la page 0...\n");
        pm.insert(7, "{\"id\":1,\"name\":\"Alice\",\"age\":25}");
        pm.insert(10, "{\"id\":2,\"name\":\"Bob\",\"age\":30}");
        pm.insert(5, "{\"id\":3,\"name\":\"Charlie\",\"age\":22}");
        pm.insert(9, "{\"id\":4,\"name\":\"David\",\"age\":28}");

        pm.printDetailedPages();

        System.out.println("\nInsertion de 8 (provoque un SPLIT)...\n");
        pm.insert(8, "{\"id\":5,\"name\":\"Eve\",\"age\":26}");

        pm.printDetailedPages();

        System.out.println("\nInsertion de 3 autres valeurs...\n");
        pm.insert(3, "{\"id\":6,\"name\":\"Frank\",\"age\":35}");
        pm.insert(12, "{\"id\":7,\"name\":\"Grace\",\"age\":29}");
        pm.insert(1, "{\"id\":8,\"name\":\"Henry\",\"age\":31}");

        pm.printDetailedPages();

        System.out.println("\n═══════════════════════════════════════════════════════════");
        System.out.println("  RÉSUMÉ");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Nombre total de pages : " + pm.getPageCount());
        System.out.println("Valeurs triées : " + pm.getAllValuesSorted());
        System.out.println("\n");
    }
}
