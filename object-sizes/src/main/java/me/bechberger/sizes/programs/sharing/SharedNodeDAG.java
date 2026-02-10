package me.bechberger.sizes.programs.sharing;

/* A tiny DAG: two parents share the same child node (vs a tree with duplicated children). */
class SharedNodeDAG {
    Root value = new Root();

    static final class Root {
        final Node shared = new Node();
        final Node left = new Node(shared);
        final Node right = new Node(shared);
    }

    static final class Node {
        final Node child;

        Node() {
            this.child = null;
        }

        Node(Node child) {
            this.child = child;
        }
    }
}