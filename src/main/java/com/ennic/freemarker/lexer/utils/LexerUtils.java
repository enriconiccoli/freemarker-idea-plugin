package com.ennic.freemarker.lexer.utils;

/**
 * Utility class containing helper methods for FreeMarker lexer operations.
 */
public final class LexerUtils {

    private LexerUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Checks if a character can start an identifier.
     */
    public static boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    /**
     * Checks if a character can be part of an identifier.
     */
    public static boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /**
     * Checks if a character is an operator character.
     */
    public static boolean isOperatorChar(char c) {
        return "+-*/=<>!&|^%".indexOf(c) >= 0;
    }

    /**
     * Checks if a string is a FreeMarker keyword.
     */
    public static boolean isFreemarkerKeyword(String word) {
        return switch (word) {
            case "if", "else", "elseif", "list", "assign", "include", "import",
                 "macro", "function", "return", "switch", "case", "default",
                 "ftl", "setting", "escape", "stop", "attempt", "recover",
                 "items", "as" -> true;
            default -> false;
        };
    }

    /**
     * Checks if buffer starts with pattern at given position.
     */
    public static boolean hasPattern(CharSequence buffer, int position, String pattern) {
        if (position + pattern.length() > buffer.length()) {
            return false;
        }

        for (int i = 0; i < pattern.length(); i++) {
            if (buffer.charAt(position + i) != pattern.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets character at position, or null character if out of bounds.
     */
    public static char safeCharAt(CharSequence buffer, int position) {
        return position < buffer.length() ? buffer.charAt(position) : '\0';
    }
}
