// Java 7 edge case: Diamond operator without try-with-resources
// Test: Diamond operator that's easy to overlook
// Expected Version: 7
// Required Features: COLLECTIONS_FRAMEWORK, DIAMOND_OPERATOR, GENERICS
class Edge_DiamondOperatorOnly {
    java.util.List<String> list = new java.util.ArrayList<>();
}