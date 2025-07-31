package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.FreeMarkerTokenTypes;
import com.ennic.freemarker.lexer.utils.LexerState;
import com.ennic.freemarker.lexer.utils.LexerUtils;

/**
 * Specialized parser for FreeMarker directives.
 */
public class DirectiveParser {

    /**
     * Parses FreeMarker directives like <#if>, </#if>, etc.
     */
    public static ParseResult parseDirective(CharSequence buffer, int currentPosition,
                                           int endOffset, LexerState currentState) {

        // Check for directive start command (<#)
        if (LexerUtils.hasPattern(buffer, currentPosition, "<#")) {
            return new ParseResult(currentPosition + 2,
                                 FreeMarkerTokenTypes.DIRECTIVE_START,
                                 LexerState.IN_DIRECTIVE);
        }

        // Check for inline directive closing command (/>)
        if (currentState == LexerState.IN_DIRECTIVE && LexerUtils.hasPattern(buffer, currentPosition, "/>")) {
            return new ParseResult(currentPosition + 2,
                                 FreeMarkerTokenTypes.DIRECTIVE_START,
                                 LexerState.NORMAL);
        }

        // Check for directive closing command (</#)
        if (LexerUtils.hasPattern(buffer, currentPosition, "</#")) {
            return new ParseResult(currentPosition + 3,
                                 FreeMarkerTokenTypes.DIRECTIVE_CLOSING,
                                 LexerState.IN_DIRECTIVE);
        }

        return null; // Not a directive
    }
}

