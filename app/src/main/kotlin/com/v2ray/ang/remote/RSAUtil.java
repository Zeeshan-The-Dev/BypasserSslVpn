package com.v2ray.ang.remote;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
    private static String privateKey = "MIIJQQIBADANBgkqhkiG9w0BAQEFAASCCSswggknAgEAAoICAQCubkbNMW8xv7Zi" +
            "ojydbC3jUKnU+rShWuTXqi/T6+M7ydYAVQ9Ij1K/BvCQzrSMrmLwjaTXYnEbpLmA" +
            "7BjU7JjWAq0T2Ax1meN3fTgIVO5LxqyGGHLq1nfoV/adTZeBpobMFh6oLW4J0+Pt" +
            "xTDL3g1g3rr5KRrmozQET0Se3j95hdtJwFWzo0YHZRIihn44oE7xuR6ecj2lst93" +
            "+at6ja4iG0Ims7jQwLdbHq8K7ClewXxAr621K81DP+b5AW0KuUejFBs/I8Xmtk/f" +
            "4SRA8Pn7MpWHihR7VlzFxWnfj9/l4JAiUa859oalYeB8bkYa5aasT2dA+N4WhV1X" +
            "v12iDyHNO8HBok9u8HZMBog7dR36TI5QhUMIOmtQX+hqC8YbkSr58Sj9Iy75MEcK" +
            "QQQE2p0CXC2oa5ef7E/TOJeJbepe+++BTEDLFZ8jpGKLZ+paM3fOn39dGlJxG92n" +
            "qNW0vZmApbKQubg3i0LU5m51YwL8OwjLxRMuF0gcrP1mrj+kMsUJosH4ZnCi0mGD" +
            "DgbzvvwxS0EmTxbkh65YaCnBYfJSLvoLs0RvYmh4VkGd1IQlwuEvABQ899ExPapc" +
            "kTiKlJSLHNXS8ZQUlxhKH4ZelmxNs43O0i6l3rlWaTvNQDY0tJiq0sO+AZ1s7Dwj" +
            "DwNHKfgGcqvlzMawaM1/nEeTgTcrKQIDAQABAoICAB/KKU0sPT+r2OmQ98sY9CQB" +
            "h3IxXsopbpt/gMf2rasv1bipx+dJd6Mo+J7rjSw2fAGrTueSIVsQYl6EqOHlXNVk" +
            "F3cOYPbt3O7h+1xJXkShKMAnpIn4HFJT9bYldf9DAj4fqeasoOlZnKiwz3vYNEf7" +
            "T8gpHaX7CHfgmnj5uEfFhXmje/sJPrIKUYvJRye6z6huPI5T87hHKUMMVZw4Rgwh" +
            "O6UiDAS/mqsYpH4xHDkQujxvyBDEstwWwVmQUPcrXUrzVyPiuSabA2Oh/KF5SZXm" +
            "WiohmCoCS1Yf7HO6HOwgSSR4Dp9ZtqBdfjQvRMeSIxY0QnUs5gzksWZUkhP+/gHE" +
            "Cz3FjjSWYpQe2dGCsxq3VunG+VE5OG2LNO2SGIOxoDUVWJvIlC5zaLGq1diWNZDM" +
            "jgRDQ4zmeUOX+ElyfjPKAVZOGtiL8hUg8JOoM1l6hLjgO3FfHQvSrVLwOmIoKfe3" +
            "x6oWZFDVDx2t1tlC0jS6eNNnc+jn6YTeXmLnjujJib5Zp000PSknieRjfIfyFcZw" +
            "NAtLQyWwNEfGrT22XGMV55+erYTPfrloLPoiEXZSy4tRo7JcY4iZlukzzh3lQlLl" +
            "OCz2tThuC2nxdV4GoGAg1KJoJyfeqh8B+fkwu1Gznr610ECko9753+zN/ofRa2WO" +
            "cxJ95KvAlEdoENcnLI8hAoIBAQDoUxIuZr/XZ/oJFOof7FuXYqgeOBO+5v3nuO/v" +
            "zqVZQY21N4E4BQlT8M+3j/Lts0OOmQupb920GSo5c1nuBdGAI8nWLpJ66hIzVlhg" +
            "XdcY7lB2lxsEYqX5gNO+aUkW1RiLZOaA9g1BmXk29LWk1cOB0AbzdR6aTtbqvr0n" +
            "Py8eEsPfag1Wc8Fj66g49WI3P+cNi16nhIm7jUCT8Vikmm29GdmPUm876iW9UDNJ" +
            "XzlY1p6StECgq1Zr3cwryaERmMCLHfe4Lp1PLGFwNKJwxq1wJuW9qbtsH9ja6mVi" +
            "HeaabPAYVJrYGxQbsaCBs6vMZ8ZUyIHQ4kSxquOdu1o6dl+XAoIBAQDANNyz88iM" +
            "TK41ZJBV88abamQIU6cYt3Ic6YB5dQXcm5rBDRDl2V5LS1FEMPQA18M3Or8GO01o" +
            "Q3nRGuVdx0TG9bgotweXOLtM6tYT6nChhSIlunEvLR2dn2h1XnsHYtg0YSwQU5Xw" +
            "f/nX2D8bYVtuV9mPcihE2iJwYqSZT1Me7h3o8euAP3cxWlYAbhZGdJsLlqtgnipf" +
            "NKQGhik4PXSZbTHVeY5cQh5S2QS2csLT975E7gvFRlOYBnXCuc6E3XA3ShIeFhhj" +
            "ycmLf1suxNwUUWhgnfMRVc+ZyQWPH6BB8QmtQN/Xn2s1uuRVK3lqhquWPpc7WlrM" +
            "kWCS3HypnCM/AoIBADqmpiNiuzHFRSE/z29puyG3vVuLqqh6JkV2sdQnpiSnYQAL" +
            "2CptUxKG69malKifpgF8763B2QWbiMZiMaTegSpJXq4cqcwwBZJCTSNe7WuU1mlp" +
            "8l8kDGS1uj8K03HZME4YlfyeoXAbL7fJBYZslOPTwb16ptQr7SPsLVKYJ+v+rsZb" +
            "PqzBomP4XUqr4DS0hD8uHSJLWn4nxdLTjh5YAjZILV16YSyOCyME+T2ftZmQ7Q16" +
            "RgqJ1tJ6dw3i5C/OEWXNlflAg2Q2fw7VquCvt59looBENlWwoI17uUaQ5+O6ds6d" +
            "pU3DYEsxh6TW2BtNY4uJ5wP164Oy8a2jW78AnVcCggEAPVwi4SgAbBnGu7DhCy+J" +
            "xe/PA4GBJLjbqzM4Go4rUuQyIVNEg7xDCBKBXR2rp+E5pxYQCbNA4Po5l3HYq3mE" +
            "EWg8ZHMZ70LjOIj30t/TGR1ha1376C2k+L79IAsLh64ci4xB/uxQP7j7T9bvPpR0" +
            "FePhxpORG3nv6KT1GyRhyswFrGoe3pBkQtMuBXEfl1p+bNKq32te54nsm1CVNCka" +
            "Yiij8hyeQAGzoIs7n+s/G2BJ08OS6mWrJitZyNJ8hZoTyvLQmd4/5wtyViBQQbwC" +
            "7lGW2tFR2cTlQ5kTNz/Hb4W9qxu82h9AoKSLZE0+s61OdeaBbSdJOWlnxTqyaYs5" +
            "DQKCAQBZ/snToTCw0C/rsG64ml2UAxKxeuBF0ETXC4tBBhMuBurHd2YTBWF/oNPJ" +
            "X7bWLwKvoXvYK/P9BvYlvZohgh7clHJ0e2ktUUB19v/Fv1W9VbakRxOh9SyTO0yw" +
            "np5WxZBepSXwcqyoZ0kYToMVL+gAcnTmshYxU7PGk9iyLRxdb7YFu7PFiOIDsxGY" +
            "eY5RfHdkTGcReP/2mB/Xv3wPZoJFvKbDPgXXfmBWybebySnCzbVoBY//iNxESuR4" +
            "G1Y8nC8WrPzDactG+lEgiUA0ZYB68eobSc1HXhuMmDzaGWY/kc7Z2JOhduImzVzm" +
            "jvY4nKl7cp88MWknVu4Lo1HJsTFs";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        }
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), stringToPrivate(privateKey));
    }

    public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
            String encryptedString = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                encryptedString = Base64.getEncoder().encodeToString(encrypt("Dhiraj is the author", publicKey));
            }
            System.out.println(encryptedString);
            String decryptedString = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                decryptedString = RSAUtil.decrypt(encryptedString);
            }
            System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }

    }


    public static PrivateKey stringToPrivate(String private_key)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {

        try {
            // Read in the key into a String
            StringBuilder pkcs8Lines = new StringBuilder();
            BufferedReader rdr = new BufferedReader(new StringReader(private_key));
            String line;
            while ((line = rdr.readLine()) != null) {
                pkcs8Lines.append(line);
            }

            // Remove the "BEGIN" and "END" lines, as well as any whitespace

            String pkcs8Pem = pkcs8Lines.toString();
            pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
            pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

            // Base64 decode the result

            byte[] pkcs8EncodedBytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);
            }

            // extract the private key

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            return privKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }
}
