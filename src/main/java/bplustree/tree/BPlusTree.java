package bplustree.tree;

public class BPlusTree<T extends Comparable<T>> {
    private final int order;
    private BPlusTreeNode<T> root;

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("L'ordre doit être au moins 3");
        }
        this.order = order;
        this.root = new BPlusTreeNode<>(order, true);
    }

    public void insert(T key) {
        SplitResult<T> result = insertInto(root, key);
        if (result != null) {
            // La racine a été splittée : créer une nouvelle racine interne
            BPlusTreeNode<T> newRoot = new BPlusTreeNode<>(order, false);
            newRoot.getKeys().add(result.promotedKey());
            newRoot.getChildren().add(result.left());
            newRoot.getChildren().add(result.right());
            root = newRoot;
        }
    }

    // Insère récursivement et retourne un SplitResult si le nœud a dû être splitté, null sinon.
    private SplitResult<T> insertInto(BPlusTreeNode<T> node, T key) {
        if (node.isLeaf()) {
            insertIntoLeaf(node, key);
            if (node.isFull()) {
                return splitLeaf(node);
            }
            return null;
        }

        // Nœud interne : trouver l'enfant à descendre
        int i = node.getKeys().size() - 1;
        while (i >= 0 && key.compareTo(node.getKeys().get(i)) < 0) {
            i--;
        }
        i++;

        SplitResult<T> childSplit = insertInto(node.getChildren().get(i), key);
        if (childSplit == null) return null;

        // L'enfant a été splitté : insérer la clé promue dans ce nœud interne
        node.getKeys().add(i, childSplit.promotedKey());
        node.getChildren().set(i, childSplit.left());
        node.getChildren().add(i + 1, childSplit.right());

        if (node.isFull()) {
            return splitInternal(node);
        }
        return null;
    }

    private void insertIntoLeaf(BPlusTreeNode<T> leaf, T key) {
        int i = leaf.getKeys().size() - 1;
        leaf.getKeys().add(null);
        while (i >= 0 && key.compareTo(leaf.getKeys().get(i)) < 0) {
            leaf.getKeys().set(i + 1, leaf.getKeys().get(i));
            i--;
        }
        leaf.getKeys().set(i + 1, key);
    }

    // Split d'une feuille : copie la clé du milieu vers le parent (B+ tree)
    private SplitResult<T> splitLeaf(BPlusTreeNode<T> leaf) {
        int mid = leaf.getKeys().size() / 2;
        BPlusTreeNode<T> right = new BPlusTreeNode<>(order, true);

        right.getKeys().addAll(leaf.getKeys().subList(mid, leaf.getKeys().size()));
        leaf.getKeys().subList(mid, leaf.getKeys().size()).clear();

        right.setNext(leaf.getNext());
        leaf.setNext(right);

        // La clé promue est une COPIE de la première clé de la feuille droite
        T promotedKey = right.getKeys().get(0);
        return new SplitResult<>(leaf, right, promotedKey);
    }

    // Split d'un nœud interne : promeut la clé du milieu (B-tree classique)
    private SplitResult<T> splitInternal(BPlusTreeNode<T> node) {
        int mid = node.getKeys().size() / 2;
        T promotedKey = node.getKeys().get(mid);

        BPlusTreeNode<T> right = new BPlusTreeNode<>(order, false);
        right.getKeys().addAll(node.getKeys().subList(mid + 1, node.getKeys().size()));
        right.getChildren().addAll(node.getChildren().subList(mid + 1, node.getChildren().size()));

        node.getKeys().subList(mid, node.getKeys().size()).clear();
        node.getChildren().subList(mid + 1, node.getChildren().size()).clear();

        return new SplitResult<>(node, right, promotedKey);
    }

    public boolean search(T key) {
        return search(root, key);
    }

    private boolean search(BPlusTreeNode<T> node, T key) {
        int i = 0;
        while (i < node.getKeys().size() && key.compareTo(node.getKeys().get(i)) > 0) {
            i++;
        }
        if (i < node.getKeys().size() && key.compareTo(node.getKeys().get(i)) == 0) {
            return true;
        }
        if (node.isLeaf()) {
            return false;
        }
        return search(node.getChildren().get(i), key);
    }

    public BPlusTreeNode<T> getRoot() {
        return root;
    }

    private record SplitResult<T extends Comparable<T>>(BPlusTreeNode<T> left, BPlusTreeNode<T> right, T promotedKey) {}
}
