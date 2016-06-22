package com.metlife.poc.util;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by palecio on 2/8/16.
 */
public final class Base64Encoding {
    /**
     * Base64 Encoding.
     */
    private Base64Encoding() {
    }

    /**
     * Encode string.
     *
     * @param key to encode
     * @return a base64 string
     */
    public static String encode(final String key) {
        final String encodeResult;
        if (key != null && key.length() > 0) {
            final byte[] encoded = Base64.encodeBase64(key.getBytes());
            encodeResult = new String(encoded);
        } else {
            encodeResult = null;
        }
        return encodeResult;
    }

    /**
     * Decode string.
     *
     * @param key to decode
     * @return a decoded string
     */
    public static String decode(final String key) {

        final String decodeResult;
        if (key != null && key.length() > 0) {
            final byte[] decoded = Base64.decodeBase64(key.getBytes());
            decodeResult = new String(decoded);
        } else {
            decodeResult = null;
        }
        return decodeResult;
    }

}
