package cmu1617.andred.pt.locmess.CryptographicOperations;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Miguel on 07/05/2017.
 */

public class CryptographicOperations {

    private static String _serverPublicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjKcI3r6Io2WIgWZxn23PsySiBEDYvUxcIwoK_ZaRsD5zuGGqz7sspGsVrWRXwLWA7KFixn9Q-8aLW1xKG2a_DODy8QjbR18v-mfTjZXFxhJtfcxBwATBadjCNyNu8Fda_oUe3ZgkK0oI0tZWFA68KADlq_U-0iC-8OCwHENoLihs5HIVWyrUL3AjXNcNmGqJ93J9qslsPM-KdCg88UxeOGCyP-uRNxZZvQneVB1RilQDQIklJA88wNtnyIMktUuenX0osxLy0Foy6cbyqeKUMbvGghmRHlKN_mpciuU5e3zYLRVPc0nLVCkr54jU3SvHiocb_bKtXJzSJ8LtOB99mQIDAQAB";
    private static byte[] _serverPublicKeyBytes = Base64.decode(_serverPublicKeyString,Base64.URL_SAFE);
    private static PublicKey _serverPublicKey = null;

    public static byte[] makeDigitalSignature(byte[] toSign, PrivateKey privateKey) {
        try {
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(privateKey);
            dsa.update(toSign);
            return dsa.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] makeDigitalSignature(byte[] toSign, byte[] privateKeyBytes) {
        try {
//            Log.wtf("Crypto", "Signing: " + Base64.encodeToString(toSign,Base64.URL_SAFE));

            KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            byte[] signed =makeDigitalSignature(toSign, privateKey);

//            Log.wtf("Crypto", "Produced: " + Base64.encodeToString(signed,Base64.URL_SAFE));
            return signed;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, PublicKey publicKey) {
        try {
//            Log.wtf("Crypto", "ciphered: " + Base64.encodeToString(cipherDigest,Base64.URL_SAFE));
//            Log.wtf("Crypto", "non ciphered: " + Base64.encodeToString(text,Base64.URL_SAFE));
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(text);
            return sig.verify(cipherDigest);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static PublicKey getServerPublicKey(){
        if(_serverPublicKey != null) return _serverPublicKey;
        KeyFactory kf = null; // or "EC" or whatever
        try {
            kf = KeyFactory.getInstance("RSA");
            _serverPublicKey = kf.generatePublic(new X509EncodedKeySpec(_serverPublicKeyBytes));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return _serverPublicKey;
    }

    public static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] text, byte[] publicKeyBytes) {
        try {
//            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
//            InputStream in = new ByteArrayInputStream(certificateKeyBytes);
//            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
//            PublicKey serverPublicKey = CryptographicOperations.getServerPublicKey();
//            cert.verify(serverPublicKey);


            KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

//            PublicKey publicKey = cert.getPublicKey();

            return verifyDigitalSignature(cipherDigest, text, publicKey);
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

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


    public static KeyPair generateKeys() {
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
