package bplustree;

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
        BPlusTreeNode<T> r = root;

        if (r.isFull()) {
            BPlusTreeNode<T> newRoot = new BPlusTreeNode<>(order, false);
            newRoot.getChildren().add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }

        insertNonFull(root, key);
    }

    private void insertNonFull(BPlusTreeNode<T> node, T key) {
        int i = node.getKeys().size() - 1;

        if (node.isLeaf()) {
            node.getKeys().add(null);
            while (i >= 0 && key.compareTo(node.getKeys().get(i)) < 0) {
                node.getKeys().set(i + 1, node.getKeys().get(i));
                i--;
            }node.getKeys().set(i + 1, key);
        } else {
            while (i >= 0 && key.compareTo(node.getKeys().get(i)) < 0) {
                i--;
            }
            i++;

            if (node.getChildren().get(i).isFull()) {
                splitChild(node, i);
                if (key.compareTo(node.getKeys().get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.getChildren().get(i), key);
        }
    }

    private void splitChild(BPlusTreeNode<T> parent, int index) {
        BPlusTreeNode<T> fullNode = parent.getChildren().get(index);
        int midIndex = (order - 1) / 2;

        BPlusTreeNode<T> newNode = new BPlusTreeNode<>(order, fullNode.isLeaf());

        T promotedKey = fullNode.getKeys().get(midIndex);

        for (int j = midIndex + (fullNode.isLeaf() ? 0 : 1); j < fullNode.getKeys().size(); j++) {
            newNode.getKeys().add(fullNode.getKeys().get(j));
        }

        if (!fullNode.isLeaf()) {
            for (int j = midIndex + 1; j < fullNode.getChildren().size(); j++) {
                newNode.getChildren().add(fullNode.getChildren().get(j));
            }
            fullNode.getChildren().subList(midIndex + 1, fullNode.getChildren().size()).clear();
        } else {
            newNode.setNext(fullNode.getNext());
            fullNode.setNext(newNode);
        }

        fullNode.getKeys().subList(midIndex + (fullNode.isLeaf() ? 0 : 1), fullNode.getKeys().size()).clear();

        parent.getKeys().add(index, promotedKey);
        parent.getChildren().add(index + 1, newNode);
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
}
