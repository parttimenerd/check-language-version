package me.bechberger.sizes.programs.circular;

/** Two nodes referencing each other: a small cycle with 2 objects. */
class TwoNodeCycle {
    Node value = new Node();

    static final class Node {
        Node other;
    }

    TwoNodeCycle() {
        Node a = new Node();
        Node b = new Node();
        a.other = b;
        b.other = a;
        // Keep a as the root.
        this.value = a;
    }
}