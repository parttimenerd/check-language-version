package me.bechberger.sizes.programs.circular;

/** Object that references itself: demonstrates that cycles don't blow up the graph. */
class SelfCycle {
    Node value = new Node();

    static final class Node {
        Node next;

        Node() {
            this.next = this;
        }
    }
}