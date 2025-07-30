package com.ennic.freemarker.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FreeMarkerLexer extends LexerBase {

    private CharSequence buffer;
    private int startOffset;
    private int endOffset;
    private int currentPosition;
    private IElementType currentToken;

    // Token states
    private static final int NORMAL = 0;
    private static final int IN_DIRECTIVE = 1;
    private static final int IN_COMMENT = 2;
    private static final int STARTING_HTML_TAG = 3;
    private static final int IN_HTML_TAG = 4;
    private int state = NORMAL;


    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.currentPosition = startOffset;
        this.state = initialState;
        advance();
    }

    @Override
    public int getState() {
        return state;
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

        if (isComment()) {
            return;
        }

        if (isDirective()){
            return;
        }

        if(isInterpolation()){
            return;
        }

        if(isString()){
            return;
        }

        if(isHTML()){
            return;
        }


        // Ending tag handling
        if (buffer.charAt(currentPosition) == '>') {
            currentPosition++;
            if (state == IN_DIRECTIVE) {
                currentToken = FreeMarkerTokenTypes.DIRECTIVE_END;
                state = NORMAL;
            } else if (state == IN_HTML_TAG) {
                currentToken = FreeMarkerTokenTypes.HTML_TAG_END;
                state = NORMAL;
            } else {
                currentToken = FreeMarkerTokenTypes.TEXT;
            }
            return;
        } else if (currentPosition < endOffset && state == IN_HTML_TAG &&
                buffer.charAt(currentPosition) == '/' && buffer.charAt(currentPosition+1) == '>') {
            currentPosition+=2;
            currentToken = FreeMarkerTokenTypes.HTML_TAG_END;
            state = NORMAL;
            return;
        }


        if (isKeyword()){
            return;
        }

        if (isOperator()){
            return;
        }

        // Text
        currentPosition++;
        currentToken = FreeMarkerTokenTypes.TEXT;
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

    private boolean isComment(){

        // Check if there's a comment start command (<#--)
        if (currentPosition + 3 < endOffset && buffer.charAt(currentPosition) == '<' &&
                buffer.charAt(currentPosition + 1) == '#' && buffer.charAt(currentPosition + 2) == '-' &&
                buffer.charAt(currentPosition + 3) == '-') {
            currentPosition += 4;
            currentToken = FreeMarkerTokenTypes.COMMENT_START;
            state = IN_COMMENT;
            return true;
        }
        if (state == IN_COMMENT && buffer.charAt(currentPosition) != '-'){
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.COMMENT_START;
            return true;
        }

        // Check if there's a comment end command (-->)
        if (currentPosition + 2 < endOffset && buffer.charAt(currentPosition) == '-' &&
                buffer.charAt(currentPosition + 1) == '-' && buffer.charAt(currentPosition + 2) == '>') {
            currentPosition += 3;
            currentToken = FreeMarkerTokenTypes.COMMENT_END;
            state = NORMAL;
            return true;
        }
        return false;
    }

    private boolean isDirective(){

        // Check if there's a directive start command (<#)
        if (currentPosition + 1 < endOffset && buffer.charAt(currentPosition) == '<' && buffer.charAt(currentPosition + 1) == '#') {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_START;
            state = IN_DIRECTIVE;
            return true;
        }

        // Check if there's an inline directive closing command (/>)
        if (state == IN_DIRECTIVE && (buffer.charAt(currentPosition) == '/' &&
                buffer.charAt(currentPosition + 1) == '>')){
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_START;
            state = NORMAL;
            return true;
        }

        // Check if there's a directive closing command (</#)
        if (currentPosition + 2 < endOffset && buffer.charAt(currentPosition) == '<' &&
                buffer.charAt(currentPosition + 1) == '/' && buffer.charAt(currentPosition + 2) == '#') {
            currentPosition += 3;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_CLOSING;
            state = IN_DIRECTIVE;
            return true;
        }
        return false;
    }

    private boolean isInterpolation(){

        // Checks if there's a starting interpolation command (${)
        if (currentPosition + 1 < endOffset && buffer.charAt(currentPosition) == '$' && buffer.charAt(currentPosition + 1) == '{') {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_START;
            return true;
        }

        // Checks if there's an ending interpolation command (})
        if (buffer.charAt(currentPosition) == '}') {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_END;
            return true;
        }
        return false;
    }

    private boolean isString(){

        if (buffer.charAt(currentPosition) == '"' || buffer.charAt(currentPosition) == '\'') {

            // Handle interpolation operation inside apos
            if ((currentPosition + 2 < endOffset && buffer.charAt(currentPosition+1) == '$' && buffer.charAt(currentPosition + 2) == '{')
                    || (buffer.charAt(currentPosition - 1) == '}')) {
                currentToken = FreeMarkerTokenTypes.STRING;
                currentPosition++;
                return true;
            }

            char quoteChar = buffer.charAt(currentPosition);
            currentPosition++;

            while (currentPosition < endOffset && buffer.charAt(currentPosition) != quoteChar) {

                if (buffer.charAt(currentPosition) == '\\' && currentPosition + 1 < endOffset) {
                    currentPosition += 2; // Skips escape and escaped characters
                } else {
                    currentPosition++;
                }
            }
            if (currentPosition < endOffset) {
                currentPosition++; // Skips end quotation mark
            }
            currentToken = FreeMarkerTokenTypes.STRING;
            return true;
        }
        return false;
    }

    private boolean isHTML(){
        // Checks if there's a starting HTML tag (<)
        if (buffer.charAt(currentPosition) == '<' && state == NORMAL) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.HTML_TAG_START;
            state = STARTING_HTML_TAG;
            return true;
        }

        // Cycle on HTML
        if (state == STARTING_HTML_TAG) {
            while (currentPosition < endOffset && (buffer.charAt(currentPosition) != '>' && buffer.charAt(currentPosition) != ' ')) {
                currentPosition++;
            }

            if (currentPosition > startOffset) {
                state = IN_HTML_TAG;
                currentToken = FreeMarkerTokenTypes.HTML_TAG_CONTENT;
                return true;
            }
        }
        return false;
    }

    private boolean isKeyword(){

        // Checks if there's a Freemarker keyword
        if (currentPosition < endOffset && (Character.isLetter(buffer.charAt(currentPosition)) ||
                buffer.charAt(currentPosition) == '_')) {
            int start = currentPosition;
            while (currentPosition < endOffset && (Character.isLetterOrDigit(buffer.charAt(currentPosition)) ||
                    buffer.charAt(currentPosition) == '_')) {
                currentPosition++;
            }

            String word = buffer.subSequence(start, currentPosition).toString();

            if (word.equals("if") || word.equals("else") || word.equals("elseif") ||
                    word.equals("list") || word.equals("assign") || word.equals("include") ||
                    word.equals("import") || word.equals("macro") || word.equals("function") ||
                    word.equals("return") || word.equals("switch") || word.equals("case") ||
                    word.equals("default") || word.equals("ftl") || word.equals("setting") ||
                    word.equals("escape") || word.equals("stop")) {
                currentToken = FreeMarkerTokenTypes.KEYWORD;
            } else {
                currentToken = FreeMarkerTokenTypes.IDENTIFIER;
            }
            return true;
        }
        return false;
    }

    private boolean isOperator(){
        // Numbers
        if (Character.isDigit(buffer.charAt(currentPosition))) {
            while (currentPosition < endOffset && (Character.isDigit(buffer.charAt(currentPosition)) ||
                    buffer.charAt(currentPosition) == '.')) {
                currentPosition++;
            }
            currentToken = FreeMarkerTokenTypes.NUMBER;
            return true;
        }

        // Operators
        if (currentPosition < endOffset && "+-*/=<>!&|^%".indexOf(buffer.charAt(currentPosition)) >= 0) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.OPERATOR;
            return true;
        }
        return false;
    }
}