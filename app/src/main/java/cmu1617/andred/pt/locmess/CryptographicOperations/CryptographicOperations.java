package cmu1617.andred.pt.locmess.CryptographicOperations;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Created by Miguel on 07/05/2017.
 */

public class CryptographicOperations {

    public static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) {
        try {
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(privateKey);
            dsa.update(bytes);
            return dsa.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(text);
            return sig.verify(cipherDigest);


        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, byte[] certificate_bytes) {
//
//        CertificateFactory cf   = null;
//        Certificate certificate = null;
//        try {
//            cf = CertificateFactory.getInstance("X.509");
//            certificate = cf.generateCertificate(new ByteArrayInputStream(certificate_bytes));
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        }
//
//
//        PublicKey key = null;
//        try {
//            key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
//            return verifyDigitalSignature(cipherDigest,text,key);
//        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public static byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }

        return result;
    }



    public static KeyPair generateKeys(){
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.initialize(1024, new SecureRandom());
        KeyPair keypair = keyGen.generateKeyPair();
        return keypair;
    }
}
