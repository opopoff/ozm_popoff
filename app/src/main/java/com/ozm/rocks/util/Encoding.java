package com.ozm.rocks.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Encoding {

    private static final String HMAC_SHA_236 = "HmacSHA256";
    private static final String UTF8 = "UTF-8";
    private static final String SIGNATURE = "d5HjIGxYEnSH5dawbOutjOjgAWhGUlXBC6iNZnpI35eNJJpkIedp8cLuHtLeOPO1";

    private Encoding() {
        // nothing;
    }

    private static String base64HmacSha256(String key, String data) throws
            NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac sha256hmac = Mac.getInstance(HMAC_SHA_236);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(UTF8), HMAC_SHA_236);
        sha256hmac.init(secretKey);
        return Base64.encodeToString(sha256hmac.doFinal(data.getBytes(UTF8)), Base64.DEFAULT);
    }

    public static String base64HmacSha256(String data) {
        try {
            return base64HmacSha256(SIGNATURE, data).replace("\n", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }

}
