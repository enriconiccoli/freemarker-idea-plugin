package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.FreeMarkerTokenTypes;
import com.ennic.freemarker.lexer.utils.LexerState;

/**
 * Specialized parser for FreeMarker comments.
 */
public class CommentParser {

    /**
     * Parses comments and TODO comments.
     *
     * @param buffer the character buffer
     * @param currentPosition current position in buffer
     * @param endOffset end of buffer
     * @param currentState current lexer state
     * @return ParseResult containing new position, token type, and state
     */
    public static ParseResult parseComment(CharSequence buffer, int currentPosition,
                                         int endOffset, LexerState currentState) {

        // Check if there's a comment start command (<#-- or <!--)
        if (currentPosition + 3 < endOffset && buffer.charAt(currentPosition) == '<' &&
                (buffer.charAt(currentPosition + 1) == '#' || buffer.charAt(currentPosition + 1) == '!') &&
                buffer.charAt(currentPosition + 2) == '-' &&
                buffer.charAt(currentPosition + 3) == '-') {

            return new ParseResult(currentPosition + 4, FreeMarkerTokenTypes.COMMENT_START, LexerState.IN_COMMENT);
        }

        // Handle content inside comments
        if (currentState == LexerState.IN_COMMENT && buffer.charAt(currentPosition) != '-') {
            if (isTodoComment(buffer, currentPosition, endOffset)) {
                return new ParseResult(currentPosition + 1, FreeMarkerTokenTypes.COMMENT_TODO, LexerState.IN_TODO);
            }
            return new ParseResult(currentPosition + 1, FreeMarkerTokenTypes.COMMENT_START, currentState);
        }

        // Check for comment end command (-->)
        if (currentPosition + 2 < endOffset && buffer.charAt(currentPosition) == '-' &&
                buffer.charAt(currentPosition + 1) == '-' && buffer.charAt(currentPosition + 2) == '>') {
            return new ParseResult(currentPosition + 3, FreeMarkerTokenTypes.COMMENT_END, LexerState.NORMAL);
        }

        // Handle TODO content
        if (currentState == LexerState.IN_TODO) {
            return new ParseResult(currentPosition + 1, FreeMarkerTokenTypes.COMMENT_TODO, currentState);
        }

        return null; // Not a comment
    }

    private static boolean isTodoComment(CharSequence buffer, int currentPosition, int endOffset) {
        int commentContentStart = currentPosition;

        // Skip whitespace at the beginning of comment
        while (commentContentStart < endOffset &&
               Character.isWhitespace(buffer.charAt(commentContentStart))) {
            commentContentStart++;
        }

        // Check if next 4 letters form "todo" (case-insensitive)
        if (commentContentStart + 4 <= endOffset) {
            String possibleTodo = buffer.subSequence(commentContentStart, commentContentStart + 4)
                                       .toString().toLowerCase();
            return "todo".equals(possibleTodo);
        }

        return false;
    }

}
