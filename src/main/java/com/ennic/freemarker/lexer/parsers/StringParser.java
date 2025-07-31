package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.FreeMarkerTokenTypes;
import com.ennic.freemarker.lexer.utils.LexerState;
import com.ennic.freemarker.lexer.utils.LexerUtils;

/**
 * Specialized parser for string literals.
 */
public class StringParser {

    /**
     * Parses string literals enclosed in quotes.
     */
    public static ParseResult parseString(CharSequence buffer, int currentPosition, int endOffset) {
        char c = LexerUtils.safeCharAt(buffer, currentPosition);
        if (c != '"' && c != '\'') {
            return null; // Not a string
        }

        // Handle interpolation context strings
        if (isInterpolationContext(buffer, currentPosition, endOffset)) {
            return new ParseResult(currentPosition + 1,
                                 FreeMarkerTokenTypes.STRING,
                                 LexerState.IN_INTERPOLATION);
        }

        return parseQuotedString(buffer, currentPosition, endOffset, c);
    }

    private static ParseResult parseQuotedString(CharSequence buffer, int currentPosition,
                                               int endOffset, char quoteChar) {
        int start = currentPosition;
        currentPosition++; // Skip opening quote

        while (currentPosition < endOffset) {
            char c = LexerUtils.safeCharAt(buffer, currentPosition);
            if (c == quoteChar) {
                currentPosition++; // Skip closing quote
                break;
            } else if (c == '\\' && currentPosition + 1 < endOffset) {
                currentPosition += 2; // Skip escape sequence
            } else {
                currentPosition++;
            }
        }

        return new ParseResult(currentPosition,
                                           FreeMarkerTokenTypes.STRING,
                                           LexerState.NORMAL);
    }

    private static boolean isInterpolationContext(CharSequence buffer, int currentPosition, int endOffset) {
        return (currentPosition + 2 < endOffset &&
                buffer.charAt(currentPosition + 1) == '$' &&
                buffer.charAt(currentPosition + 2) == '{') ||
               (currentPosition > 0 && buffer.charAt(currentPosition - 1) == '}');
    }
}
