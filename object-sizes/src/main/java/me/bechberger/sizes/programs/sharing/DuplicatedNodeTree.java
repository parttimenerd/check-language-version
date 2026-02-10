package me.bechberger.sizes.programs.sharing;

/* Similar shape as SharedNodeDAG, but without sharing: each parent has its own child. */
class DuplicatedNodeTree {
    Root value = new Root();

    static final class Root {
        final Node left = new Node(new Node());
        final Node right = new Node(new Node());
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