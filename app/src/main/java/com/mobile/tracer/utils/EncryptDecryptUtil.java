package com.mobile.tracer.utils;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptDecryptUtil {
    private EncryptDecryptUtil(){
        //nothing to do.
    }
    public static SecretKey generateKey(String mySecret){
        return new SecretKeySpec(mySecret.getBytes(), "AES");
    }

    public static String encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        /* Encrypt the message. */
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv)  );
        byte[] cipherText = cipher.doFinal(message.getBytes("utf-8"));
        byte[] ivAndCipherText = getCombinedArray(iv, cipherText);
        return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);
    }

    public static String decrypt(String encoded, SecretKey secret) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (!TextUtils.isEmpty(encoded)) {
            byte[] ivAndCipherText = Base64.decode(encoded, Base64.NO_WRAP);
            byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
            byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            return new String(cipher.doFinal(cipherText), "UTF-8");
        }
        return "Sorry! Encrypted Message is Empty";
    }

    private static byte[] getCombinedArray(byte[] one, byte[] two) {
        byte[] combined = new byte[one.length + two.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        return combined;
    }


    public static String getSecuredResponseObject(String jsonType, SecretKey secretKey) throws UnsupportedEncodingException {
        String decryptedResponse = "";
        try {
            decryptedResponse = EncryptDecryptUtil.decrypt(jsonType, secretKey);
        } catch (Exception e) {
//            GenericUtilities.handleException(e);
        }
        return decryptedResponse;
    }

    public static String getDecryptedKeys(String enKey, SecretKey secretKey) throws JSONException {
        String decry = "";
        try {
            decry = EncryptDecryptUtil.decrypt(enKey,secretKey);
        } catch (Exception e) {
//          Logger.log
        }

        return decry;
    }

}

