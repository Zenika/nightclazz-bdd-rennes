package bplustree.tree;

import bplustree.index.IndexEntry;

import java.util.ArrayList;
import java.util.List;

public class TreePrinter {

    // Représente un nœud positionné : offset de départ (dans la ligne) et largeur occupée
    private record Block(int offset, int width, String label) {
        int center() { return offset + width / 2; }
    }

    public static String print(BPlusTreeNode<IndexEntry> root, int treeOrder) {
        if (root == null) return "(arbre vide)";

        StringBuilder sb = new StringBuilder();
        sb.append("\n  Arbre B+ ordre ").append(treeOrder).append("\n\n");

        // Construire les lignes de l'arbre
        List<String> lines = buildLines(root);
        for (String line : lines) {
            sb.append("  ").append(line).append("\n");
        }

        return sb.toString();
    }

    // Construit toutes les lignes (nœuds + branches) via un parcours BFS
    private static List<String> buildLines(BPlusTreeNode<IndexEntry> root) {
        // Étape 1 : collecter les niveaux
        List<List<BPlusTreeNode<IndexEntry>>> levels = new ArrayList<>();
        List<BPlusTreeNode<IndexEntry>> current = List.of(root);
        while (!current.isEmpty()) {
            levels.add(current);
            List<BPlusTreeNode<IndexEntry>> next = new ArrayList<>();
            for (BPlusTreeNode<IndexEntry> node : current) {
                if (!node.isLeaf()) next.addAll(node.getChildren());
            }
            current = next;
        }

        // Étape 2 : calculer la largeur totale à partir des feuilles
        List<BPlusTreeNode<IndexEntry>> leaves = levels.get(levels.size() - 1);
        int leafSlot = computeLeafSlot(leaves);
        int totalWidth = leafSlot * leaves.size();

        // Étape 3 : calculer les blocs (offset + width) de chaque nœud, niveau par niveau
        // On part des feuilles et on remonte
        List<List<Block>> blockLevels = new ArrayList<>();

        // Feuilles : réparties uniformément
        List<Block> leafBlocks = new ArrayList<>();
        for (int i = 0; i < leaves.size(); i++) {
            String label = nodeLabel(leaves.get(i));
            int offset = i * leafSlot;
            // centrer le label dans le slot
            int pad = leafSlot - label.length();
            int leftPad = pad / 2;
            leafBlocks.add(new Block(offset + leftPad, label.length(), label));
        }
        blockLevels.addFirst(leafBlocks);

        // Niveaux intermédiaires et racine : centrer chaque parent sur ses enfants
        int childLevelIdx = levels.size() - 1;
        List<Block> childBlocks = leafBlocks;

        for (int lvl = levels.size() - 2; lvl >= 0; lvl--) {
            List<BPlusTreeNode<IndexEntry>> levelNodes = levels.get(lvl);
            List<Block> parentBlocks = new ArrayList<>();

            // Distribuer les blocs enfants aux parents
            int childIdx = 0;
            for (BPlusTreeNode<IndexEntry> node : levelNodes) {
                int childCount = node.isLeaf() ? 0 : node.getChildren().size();
                String label = nodeLabel(node);

                if (childCount == 0) {
                    // Ne devrait pas arriver sauf pour un arbre à 1 nœud
                    parentBlocks.add(new Block(0, label.length(), label));
                } else {
                    // Centrer le parent entre le premier et le dernier enfant
                    Block firstChild = childBlocks.get(childIdx);
                    Block lastChild  = childBlocks.get(childIdx + childCount - 1);
                    int spanStart = firstChild.center();
                    int spanEnd   = lastChild.center();
                    int center    = (spanStart + spanEnd) / 2;
                    int offset    = center - label.length() / 2;
                    parentBlocks.add(new Block(offset, label.length(), label));
                    childIdx += childCount;
                }
            }
            blockLevels.addFirst(parentBlocks);
            childBlocks = parentBlocks;
        }

        // Étape 4 : rendre les lignes de texte (nœuds + branches)
        List<String> lines = new ArrayList<>();
        for (int lvl = 0; lvl < blockLevels.size(); lvl++) {
            // Ligne des nœuds
            lines.add(renderNodeLine(blockLevels.get(lvl), totalWidth));

            // Ligne des branches (sauf après les feuilles)
            if (lvl < blockLevels.size() - 1) {
                lines.add(renderBranchLine(
                        blockLevels.get(lvl),
                        blockLevels.get(lvl + 1),
                        levels.get(lvl),
                        totalWidth
                ));
            }
        }
        return lines;
    }

    // Largeur minimale d'un slot de feuille
    private static int computeLeafSlot(List<BPlusTreeNode<IndexEntry>> leaves) {
        int max = 0;
        for (BPlusTreeNode<IndexEntry> leaf : leaves) {
            max = Math.max(max, nodeLabel(leaf).length());
        }
        return max + 3; // marges
    }

    // Rend une ligne de nœuds
    private static String renderNodeLine(List<Block> blocks, int totalWidth) {
        char[] buf = spaces(totalWidth);
        for (Block b : blocks) {
            placeString(buf, b.offset(), b.label());
        }
        return new String(buf).stripTrailing();
    }

    // Rend une ligne de branches /\ entre les parents (lvl) et les enfants (lvl+1)
    private static String renderBranchLine(
            List<Block> parentBlocks,
            List<Block> childBlocks,
            List<BPlusTreeNode<IndexEntry>> parentNodes,
            int totalWidth
    ) {
        char[] buf = spaces(totalWidth);

        int childIdx = 0;
        for (int pi = 0; pi < parentNodes.size(); pi++) {
            BPlusTreeNode<IndexEntry> parent = parentNodes.get(pi);
            if (parent.isLeaf()) continue;

            int parentCenter = parentBlocks.get(pi).center();
            int childCount   = parent.getChildren().size();

            for (int ci = 0; ci < childCount; ci++) {
                int childCenter = childBlocks.get(childIdx + ci).center();
                if (childCenter < parentCenter) {
                    buf[childCenter] = '/';
                } else if (childCenter > parentCenter) {
                    buf[childCenter] = '\\';
                } else {
                    buf[childCenter] = '|';
                }
            }
            childIdx += childCount;
        }
        return new String(buf).stripTrailing();
    }

    private static char[] spaces(int width) {
        char[] buf = new char[width];
        java.util.Arrays.fill(buf, ' ');
        return buf;
    }

    private static void placeString(char[] buf, int offset, String s) {
        for (int i = 0; i < s.length() && offset + i < buf.length; i++) {
            buf[offset + i] = s.charAt(i);
        }
    }

    private static String nodeLabel(BPlusTreeNode<IndexEntry> node) {
        StringBuilder sb = new StringBuilder();
        List<IndexEntry> keys = node.getKeys();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) sb.append(" | ");
            sb.append(keys.get(i).key().value());
        }
        return sb.toString();
    }

}
