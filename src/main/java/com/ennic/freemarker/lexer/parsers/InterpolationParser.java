package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.FreeMarkerTokenTypes;
import com.ennic.freemarker.lexer.utils.LexerState;
import com.ennic.freemarker.lexer.utils.LexerUtils;

/**
 * Specialized parser for FreeMarker interpolations and expression.
 */
public class InterpolationParser {

    /**
     * Parses interpolation expressions ${...} and expression within them.
     */
    public static ParseResult parseInterpolation(CharSequence buffer, int currentPosition,
                                               int endOffset, LexerState currentState) {

        // Check for starting interpolation command (${)
        if (LexerUtils.hasPattern(buffer, currentPosition, "${")) {
            return new ParseResult(currentPosition + 2,
                                 FreeMarkerTokenTypes.INTERPOLATION_START,
                                 LexerState.IN_INTERPOLATION);
        }

        // Check for ending interpolation command (})
        if (LexerUtils.safeCharAt(buffer, currentPosition) == '}') {
            return new ParseResult(currentPosition + 1,
                                 FreeMarkerTokenTypes.INTERPOLATION_END,
                                 LexerState.NORMAL);
        }

        // Check for expression within interpolation
        return parseExpression(buffer, currentPosition, endOffset);// Not an interpolation
    }

    private static ParseResult parseExpression(CharSequence buffer, int currentPosition, int endOffset) {
        char currentChar = LexerUtils.safeCharAt(buffer, currentPosition);

        // Check if current character is a dot followed by an identifier
        if (currentChar == '.') {
            int start = currentPosition;
            currentPosition++; // skip the dot

            // Verify there's an identifier after the dot
            if (currentPosition < endOffset && LexerUtils.isIdentifierStart(LexerUtils.safeCharAt(buffer, currentPosition))) {

                while (currentPosition < endOffset && LexerUtils.isIdentifierPart(LexerUtils.safeCharAt(buffer, currentPosition))) {
                    currentPosition++;
                }

                // Verify there's an opening parenthesis (indicates function call)
                //if (currentPosition < endOffset && LexerUtils.safeCharAt(buffer, currentPosition) == '(') {
                    // Only tokenize the dot + expression, leave parentheses for separate parsing
                    return new ParseResult(currentPosition,
                                                       FreeMarkerTokenTypes.EXPRESSION,
                                                       LexerState.IN_INTERPOLATION);
                //}
            }
        }

        return null; // Not am expression
    }
}
