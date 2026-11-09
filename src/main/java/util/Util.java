package fisch.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Util {

    private Util() {
    }

    /** Escape HTML để in ra trang an toàn. */
    public static String esc(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Escape chuỗi để đặt trong "..." của JavaScript. */
    public static String js(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'")
                .replace("\n", "\\n").replace("\r", "").replace("<", "\\x3C");
    }

    public static int toInt(String s, int macDinh) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return macDinh;
        }
    }

    public static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
