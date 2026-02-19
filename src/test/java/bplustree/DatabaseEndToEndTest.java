package bplustree;

import bplustree.database.Database;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Test;

class DatabaseEndToEndTest {

    // =========================================================================
    //  Scénario 1 : IDs séquentiels — vérification visuelle pour les ordres 3, 4 et 5
    // =========================================================================

    @ParameterizedTest
    @BPlusTreeOrderTest(orders = {3, 4, 5})
    void catalogue_de_produits_avec_ids_sequentiels(int ordre) {

        var db = Database.withSequentialIds(ordre, /* taille de page */ 4);

        System.out.println("\ninsert { nom: Casque audio, prix: 89.99 }");
        System.out.println(db.insert("""
                { "nom": "Casque audio", "prix": 89.99, "categorie": "electronique" }
                """.strip()));

        System.out.println("insert { nom: Clavier mecanique, prix: 129.00 }");
        var clavier = db.insert("""
                { "nom": "Clavier mecanique", "prix": 129.00, "categorie": "electronique" }
                """.strip());
        System.out.println(clavier);

        System.out.println("insert { nom: Souris ergonomique, prix: 49.50 }");
        var souris = db.insert("""
                { "nom": "Souris ergonomique", "prix": 49.50, "categorie": "electronique" }
                """.strip());
        System.out.println(souris);

        System.out.println("insert { nom: Bureau debout, prix: 350.00 }");
        System.out.println(db.insert("""
                { "nom": "Bureau debout", "prix": 350.00, "categorie": "mobilier" }
                """.strip()));

        System.out.println("insert { nom: Lampe LED, prix: 25.00 }");
        System.out.println(db.insert("""
                { "nom": "Lampe LED", "prix": 25.00, "categorie": "luminaire" }
                """.strip()));

        db.printTree();

        System.out.println("select id = " + souris.id());
        System.out.println(db.findById(souris.id()));
    }

    // =========================================================================
    //  Scénario 2 : IDs UUID v4 — désordonnés, provoque des splits de pages
    // =========================================================================

    @ParameterizedTest
    @BPlusTreeOrderTest(orders = {3, 4, 5})
    void journal_de_commandes_avec_ids_uuidv4(int ordre) {

        var db = Database.withUuidV4Ids(ordre, /* taille de page */ 4);

        System.out.println("\ninsert { client: Alice, montant: 120.00 }");
        System.out.println(db.insert("""
                { "client": "Alice", "montant": 120.00, "statut": "expediee" }
                """.strip()));

        System.out.println("insert { client: Bob, montant: 45.50 }");
        System.out.println(db.insert("""
                { "client": "Bob", "montant": 45.50, "statut": "en_attente" }
                """.strip()));

        System.out.println("insert { client: Charlie, montant: 890.00 }");
        var charlie = db.insert("""
                { "client": "Charlie", "montant": 890.00, "statut": "livree" }
                """.strip());
        System.out.println(charlie);

        System.out.println("insert { client: Diana, montant: 15.00 }");
        System.out.println(db.insert("""
                { "client": "Diana", "montant": 15.00, "statut": "annulee" }
                """.strip()));

        System.out.println("insert { client: Eve, montant: 230.00 }");
        System.out.println(db.insert("""
                { "client": "Eve", "montant": 230.00, "statut": "expediee" }
                """.strip()));

        System.out.println("insert { client: Frank, montant: 75.00 }");
        System.out.println(db.insert("""
                { "client": "Frank", "montant": 75.00, "statut": "en_attente" }
                """.strip()));

        db.printTree();

        System.out.println("select id = " + charlie.id());
        System.out.println(db.findById(charlie.id()));
    }

}
