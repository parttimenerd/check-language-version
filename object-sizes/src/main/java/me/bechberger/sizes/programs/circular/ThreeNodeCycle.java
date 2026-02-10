package me.bechberger.sizes.programs.circular;

/* Three nodes referencing each other in a cycle: A->B->C->A. */
class ThreeNodeCycle {
    Node value;

    static final class Node {
        Node next;
    }

    ThreeNodeCycle() {
        Node a = new Node();
        Node b = new Node();
        Node c = new Node();
        a.next = b;
        b.next = c;
        c.next = a;
        value = a;
    }
}