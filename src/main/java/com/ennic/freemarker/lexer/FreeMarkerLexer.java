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
    private static final int IN_TODO = 5;
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

        // Check if there's a comment start command (<#-- or <!--)
        if (currentPosition + 3 < endOffset && buffer.charAt(currentPosition) == '<' &&
                (buffer.charAt(currentPosition + 1) == '#' || buffer.charAt(currentPosition + 1) == '!') && buffer.charAt(currentPosition + 2) == '-' &&
                buffer.charAt(currentPosition + 3) == '-') {
            currentPosition += 4;
            currentToken = FreeMarkerTokenTypes.COMMENT_START;
            state = IN_COMMENT;
            return true;
        }

        if (state == IN_COMMENT && buffer.charAt(currentPosition) != '-'){

            if (isTodoComment()) {
                currentPosition++;
                state = IN_TODO;
                currentToken = FreeMarkerTokenTypes.COMMENT_TODO;
                return true;
            }

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

        if (state == IN_TODO){
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.COMMENT_TODO;
            return true;
        }

        return false;
    }

    // Check for "todo"
    private boolean isTodoComment() {
        int commentContentStart = currentPosition;

        while (commentContentStart < endOffset &&
               Character.isWhitespace(buffer.charAt(commentContentStart))) {
            commentContentStart++;
        }

        if (commentContentStart + 4 <= endOffset) {
            String possibleTodo = buffer.subSequence(commentContentStart, commentContentStart + 4).toString().toLowerCase();
            return "todo".equals(possibleTodo);
        }

        return false;
    }

    private boolean isDirective() {
        // Check for directive start command (<#)
        if (hasPattern("<#", 2)) {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_START;
            state = IN_DIRECTIVE;
            return true;
        }

        // Check for inline directive closing command (/>)
        if (state == IN_DIRECTIVE && hasPattern("/>", 2)) {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_START;
            state = NORMAL;
            return true;
        }

        // Check for directive closing command (</#)
        if (hasPattern("</#", 3)) {
            currentPosition += 3;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_CLOSING;
            state = IN_DIRECTIVE;
            return true;
        }
        return false;
    }

    private boolean isInterpolation() {
        // Check for starting interpolation command (${)
        if (hasPattern("${", 2)) {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_START;
            return true;
        }

        // Check for ending interpolation command (})
        if (currentChar() == '}') {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_END;
            return true;
        }

        // Check for function call
        return isFunctionCall();
    }

    private boolean isFunctionCall() {

        if (currentChar() == '.') {
            int start = currentPosition;
            currentPosition++;

            if (currentPosition < endOffset && isIdentifierStart(currentChar())) {

                while (currentPosition < endOffset && isIdentifierPart(currentChar())) {
                    currentPosition++;
                }

                currentToken = FreeMarkerTokenTypes.FUNCTION_CALL;
                return true;
            }
            currentPosition = start;
        }

        return false;
    }

    private boolean isString() {
        char c = currentChar();
        if (c != '"' && c != '\'') {
            return false;
        }

        // Handle interpolation inside quotes
        if (isInterpolationContext()) {
            currentToken = FreeMarkerTokenTypes.STRING;
            currentPosition++;
            return true;
        }

        return parseQuotedString(c);
    }

    private boolean parseQuotedString(char quoteChar) {
        currentPosition++; // Skip opening quote

        while (currentPosition < endOffset) {
            char c = currentChar();
            if (c == quoteChar) {
                currentPosition++; // Skip closing quote
                break;
            } else if (c == '\\' && currentPosition + 1 < endOffset) {
                currentPosition += 2; // Skip escape sequence
            } else {
                currentPosition++;
            }
        }

        currentToken = FreeMarkerTokenTypes.STRING;
        return true;
    }

    private boolean isInterpolationContext() {
        return (currentPosition + 2 < endOffset &&
                buffer.charAt(currentPosition + 1) == '$' &&
                buffer.charAt(currentPosition + 2) == '{') ||
               (currentPosition > 0 && buffer.charAt(currentPosition - 1) == '}');
    }

    private boolean isHTML() {
        // Check for HTML tag start (<)
        if (currentChar() == '<' && state == NORMAL) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.HTML_TAG_START;
            state = STARTING_HTML_TAG;
            return true;
        }

        // Parse HTML tag content
        if (state == STARTING_HTML_TAG) {
            int start = currentPosition;
            while (currentPosition < endOffset &&
                   currentChar() != '>' &&
                   currentChar() != ' ') {
                currentPosition++;
            }

            if (currentPosition > start) {
                state = IN_HTML_TAG;
                currentToken = FreeMarkerTokenTypes.HTML_TAG_CONTENT;
                return true;
            }
        }
        return false;
    }

    private boolean isKeyword() {
        if (!isIdentifierStart(currentChar())) {
            return false;
        }

        int start = currentPosition;
        while (currentPosition < endOffset && isIdentifierPart(currentChar())) {
            currentPosition++;
        }

        String word = buffer.subSequence(start, currentPosition).toString();
        currentToken = isFreemarkerKeyword(word) ?
            FreeMarkerTokenTypes.KEYWORD :
            FreeMarkerTokenTypes.IDENTIFIER;

        return true;
    }

    private boolean isFreemarkerKeyword(String word) {
        if (state == IN_HTML_TAG || state == IN_COMMENT || currentToken == FreeMarkerTokenTypes.TEXT) {
            return false;
        }

        return switch (word) {
            case "if", "else", "elseif", "list", "assign", "include", "import", "macro", "function", "return", "switch",
                 "case", "default", "ftl", "setting", "escape", "stop", "attempt", "recover", "items", "as"  -> true;
            default -> false;
        };
    }

    private boolean isOperator() {
        // Handle numbers
        if (Character.isDigit(currentChar())) {
            parseNumber();
            currentToken = FreeMarkerTokenTypes.NUMBER;
            return true;
        }

        // Handle operators
        if (isOperatorChar(currentChar())) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.OPERATOR;
            return true;
        }

        return false;
    }

    private void parseNumber() {
        while (currentPosition < endOffset) {
            char c = currentChar();
            if (Character.isDigit(c) || c == '.') {
                currentPosition++;
            } else {
                break;
            }
        }
    }

    // Helper methods for better readability
    private char currentChar() {
        return currentPosition < endOffset ? buffer.charAt(currentPosition) : '\0';
    }

    private boolean hasPattern(String pattern, int length) {
        return currentPosition + length - 1 < endOffset &&
               buffer.toString().startsWith(pattern, currentPosition);
    }

    private boolean isIdentifierStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isIdentifierPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean isOperatorChar(char c) {
        return "+-*/=<>!&|^%".indexOf(c) >= 0;
    }
}