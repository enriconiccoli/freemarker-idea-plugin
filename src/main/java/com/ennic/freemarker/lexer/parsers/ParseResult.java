package com.ennic.freemarker.lexer.parsers;

import com.ennic.freemarker.lexer.utils.LexerState;
import com.intellij.psi.tree.IElementType;

/**
 * Result of parsing operation.
 */
public class ParseResult {
    public final int newPosition;
    public final IElementType tokenType;
    public final LexerState newState;

    public ParseResult(int newPosition, IElementType tokenType, LexerState newState) {
        this.newPosition = newPosition;
        this.tokenType = tokenType;
        this.newState = newState;
    }
}