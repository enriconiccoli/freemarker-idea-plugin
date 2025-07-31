package com.ennic.freemarker.lexer.utils;

/**
 * Enumeration of possible lexer states for FreeMarker parsing.
 */
public enum LexerState {
    NORMAL(0),
    IN_DIRECTIVE(1),
    IN_COMMENT(2),
    STARTING_HTML_TAG(3),
    IN_HTML_TAG(4),
    IN_TODO(5),
    IN_INTERPOLATION(6);

    private final int value;

    LexerState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LexerState fromValue(int value) {
        for (LexerState state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        return NORMAL;
    }
}
