package bplustree.tree;

import java.util.ArrayList;
import java.util.List;

class BPlusTreeNode<T extends Comparable<T>> {
    private final int order;
    private final List<T> keys;
    private final List<BPlusTreeNode<T>> children;
    private BPlusTreeNode<T> next; // Pour les feuilles
    private final boolean isLeaf;

    public BPlusTreeNode(int order, boolean isLeaf) {
        this.order = order;
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }

    public List<T> getKeys() {
        return keys;
    }

    public List<BPlusTreeNode<T>> getChildren() {
        return children;
    }

    public BPlusTreeNode<T> getNext() {
        return next;
    }

    public void setNext(BPlusTreeNode<T> next) {
        this.next = next;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isFull() {
        return keys.size() >= order - 1;
    }
}
