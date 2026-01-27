// Java 2 edge case: strictfp on interface
// Test: strictfp interface declaration
// Expected Version: 2
// Required Features: STRICTFP
strictfp interface Edge_StrictfpInterface { double calc(); }
public class Tiny_StrictfpIface_Java2 implements Edge_StrictfpInterface {
    public double calc() { return 1.0; }
}