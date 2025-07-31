package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.FreeMarkerTokenTypes;
import com.ennic.freemarker.lexer.utils.LexerState;
import com.ennic.freemarker.lexer.utils.LexerUtils;

/**
 * Specialized parser for HTML tags.
 */
public class HtmlParser {

    /**
     * Parses HTML tags and their content.
     */
    public static ParseResult parseHtml(CharSequence buffer, int currentPosition,
                                      int endOffset, LexerState currentState) {

        // Check for HTML tag start (<)
        if (LexerUtils.safeCharAt(buffer, currentPosition) == '<' && currentState == LexerState.NORMAL) {
            return new ParseResult(currentPosition + 1,
                                 FreeMarkerTokenTypes.HTML_TAG_START,
                                 LexerState.STARTING_HTML_TAG);
        }

        // Parse HTML tag content
        if (currentState == LexerState.STARTING_HTML_TAG) {
            int start = currentPosition;
            while (currentPosition < endOffset &&
                   LexerUtils.safeCharAt(buffer, currentPosition) != '>' &&
                   LexerUtils.safeCharAt(buffer, currentPosition) != ' ') {
                currentPosition++;
            }

            if (currentPosition > start) {
                return new ParseResult(currentPosition,
                                     FreeMarkerTokenTypes.HTML_TAG_CONTENT,
                                     LexerState.IN_HTML_TAG);
            }
        }

        return null; // Not HTML
    }
}
