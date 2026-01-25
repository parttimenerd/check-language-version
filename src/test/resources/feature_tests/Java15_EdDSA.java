// Java 15 feature: Edwards-Curve Digital Signature Algorithm (JEP 339)
// Expected Version: 15
// Required Features: EDDSA
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.EdECPublicKey;
import java.security.interfaces.EdECPrivateKey;

public class Java15_EdDSA {
    public void testEdDSA() throws Exception {
        // Generate EdDSA key pair (Ed25519)
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        KeyPair keyPair = kpg.generateKeyPair();

        // Use explicit EdDSA types for detection
        EdECPrivateKey privateKey = (EdECPrivateKey) keyPair.getPrivate();
        EdECPublicKey publicKey = (EdECPublicKey) keyPair.getPublic();

        // Sign data
        Signature sig = Signature.getInstance("Ed25519");
        sig.initSign(privateKey);
        byte[] message = "Hello, EdDSA!".getBytes();
        sig.update(message);
        byte[] signature = sig.sign();

        System.out.println("Signature length: " + signature.length);

        // Verify signature
        sig.initVerify(publicKey);
        sig.update(message);
        boolean valid = sig.verify(signature);

        System.out.println("Signature valid: " + valid);
    }
}