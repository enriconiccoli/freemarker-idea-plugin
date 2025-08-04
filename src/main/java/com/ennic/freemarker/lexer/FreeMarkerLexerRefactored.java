package com.ennic.freemarker.lexer;

import com.ennic.freemarker.lexer.parsers.*;
import com.ennic.freemarker.lexer.utils.LexerState;
import com.ennic.freemarker.lexer.utils.LexerUtils;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Refactored FreeMarker lexer with improved structure and readability.
 *
 * This lexer tokenizes FreeMarker templates by delegating specific parsing
 * tasks to specialized parser classes.
 */
public class FreeMarkerLexerRefactored extends LexerBase {

    // Buffer and position management
    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentPosition;
    private IElementType currentToken;
    private LexerState state = LexerState.NORMAL;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentPosition = startOffset;
        this.state = LexerState.fromValue(initialState);
        advance();
    }

    @Override
    public int getState() {
        return state.getValue();
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return currentToken;
    }

    @Override
    public int getTokenStart() {
        return startOffset;
    }

    @Override
    public int getTokenEnd() {
        return currentPosition;
    }

    @Override
    public void advance() {
        if (currentPosition >= endOffset) {
            currentToken = null;
            return;
        }

        startOffset = currentPosition;

        // Try parsing different token types in priority order
        if (tryParseComment()) return;
        if (tryParseDirective()) return;
        if (tryParseInterpolation()) return;
        if (tryParseString()) return;
        if (tryParseHtml()) return;
        if (tryParseTagEnding()) return;
        if (tryParseKeyword()) return;
        if (tryParseOperatorOrNumber()) return;

        // Default: treat as text
        parseAsText();
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }

    // ============ PARSING METHODS ============

    private boolean tryParseComment() {
        ParseResult result = CommentParser.parseComment(buffer, currentPosition, endOffset, state);
        if (result != null) {
            applyParseResult(result);
            return true;
        }
        return false;
    }

    private boolean tryParseDirective() {
        ParseResult result = DirectiveParser.parseDirective(buffer, currentPosition, endOffset, state);
        if (result != null) {
            applyParseResult(result);
            return true;
        }
        return false;
    }

    private boolean tryParseInterpolation() {
        ParseResult result = InterpolationParser.parseInterpolation(buffer, currentPosition, endOffset, state);
        if (result != null) {
            applyParseResult(result);
            return true;
        }
        return false;
    }

    private boolean tryParseString() {
        ParseResult result = StringParser.parseString(buffer, currentPosition, endOffset, state);
        if (result != null) {
            applyParseResult(result);
            return true;
        }
        return false;
    }

    private boolean tryParseHtml() {
        ParseResult result = HtmlParser.parseHtml(buffer, currentPosition, endOffset, state);
        if (result != null) {
            applyParseResult(result);
            return true;
        }
        return false;
    }

    private boolean tryParseTagEnding() {
        char currentChar = LexerUtils.safeCharAt(buffer, currentPosition);

        // Handle '>' ending tags
        if (currentChar == '>') {
            currentPosition++;
            if (state == LexerState.IN_DIRECTIVE || state == LexerState.IN_INTERPOLATION) {
                currentToken = FreeMarkerTokenTypes.DIRECTIVE_END;
                state = LexerState.NORMAL;
            } else if (state == LexerState.IN_HTML_TAG) {
                currentToken = FreeMarkerTokenTypes.HTML_TAG_END;
                state = LexerState.NORMAL;
            } else {
                currentToken = FreeMarkerTokenTypes.TEXT;
            }
            return true;
        }

        // Handle '/>' self-closing HTML tags
        if ((state == LexerState.IN_HTML_TAG || state == LexerState.IN_INTERPOLATION) &&
            currentChar == '/' &&
            LexerUtils.safeCharAt(buffer, currentPosition + 1) == '>') {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.HTML_TAG_END;
            state = LexerState.NORMAL;
            return true;
        }

        return false;
    }

    private boolean tryParseKeyword() {
        if (!LexerUtils.isIdentifierStart(LexerUtils.safeCharAt(buffer, currentPosition))) {
            return false;
        }

        int start = currentPosition;
        while (currentPosition < endOffset &&
               LexerUtils.isIdentifierPart(LexerUtils.safeCharAt(buffer, currentPosition))) {
            currentPosition++;
        }

        String word = buffer.subSequence(start, currentPosition).toString();

        // Don't treat as keyword if we're in certain contexts
        if (shouldIgnoreKeywords()) {
            currentToken = FreeMarkerTokenTypes.IDENTIFIER;
        } else {
            currentToken = LexerUtils.isFreemarkerKeyword(word) ?
                FreeMarkerTokenTypes.KEYWORD :
                FreeMarkerTokenTypes.IDENTIFIER;
        }

        return true;
    }

    private boolean shouldIgnoreKeywords() {
        return state == LexerState.IN_HTML_TAG ||
               state == LexerState.IN_COMMENT ||
               currentToken == FreeMarkerTokenTypes.TEXT;
    }

    private boolean tryParseOperatorOrNumber() {
        char currentChar = LexerUtils.safeCharAt(buffer, currentPosition);

        // Handle numbers
        if (Character.isDigit(currentChar)) {
            parseNumber();
            currentToken = FreeMarkerTokenTypes.NUMBER;
            return true;
        }

        // Handle operators
        if (LexerUtils.isOperatorChar(currentChar)) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.OPERATOR;
            return true;
        }

        return false;
    }

    private void parseNumber() {
        while (currentPosition < endOffset) {
            char c = LexerUtils.safeCharAt(buffer, currentPosition);
            if (Character.isDigit(c) || c == '.') {
                currentPosition++;
            } else {
                break;
            }
        }
    }

    private void parseAsText() {
        currentPosition++;
        currentToken = FreeMarkerTokenTypes.TEXT;
    }

    private void applyParseResult(ParseResult result) {
        this.currentPosition = result.newPosition;
        this.currentToken = result.tokenType;
        this.state = result.newState;
    }
}
