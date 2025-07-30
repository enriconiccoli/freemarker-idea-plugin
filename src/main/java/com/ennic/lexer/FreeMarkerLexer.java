package com.ennic.lexer;

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

    // Stati del lexer
    private static final int NORMAL = 0;
    private static final int IN_DIRECTIVE = 1;
    private static final int IN_CLOSING_DIRECTIVE = 2;
    private static final int IN_HTML_TAG = 3;
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

        // Controlla per i tag di direttiva
        if (currentPosition + 1 < endOffset && buffer.charAt(currentPosition) == '<' && buffer.charAt(currentPosition + 1) == '#') {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_START;
            state = IN_DIRECTIVE;
            return;
        }

        // Controlla per i tag di chiusura direttiva
        if (currentPosition + 2 < endOffset && buffer.charAt(currentPosition) == '<' &&
                buffer.charAt(currentPosition + 1) == '/' && buffer.charAt(currentPosition + 2) == '#') {
            currentPosition += 3;
            currentToken = FreeMarkerTokenTypes.DIRECTIVE_CLOSING;
            state = IN_CLOSING_DIRECTIVE;
            return;
        }

        // Gestione della fine di un tag in base allo stato
        if (buffer.charAt(currentPosition) == '>') {
            currentPosition++;
            if (state == IN_DIRECTIVE || state == IN_CLOSING_DIRECTIVE) {
                currentToken = FreeMarkerTokenTypes.DIRECTIVE_END;
                state = NORMAL;
            } else if (state == IN_HTML_TAG) {
                currentToken = FreeMarkerTokenTypes.HTML_TAG_END;
                state = NORMAL;
            } else {
                // '>' normale non in un tag
                currentToken = FreeMarkerTokenTypes.TEXT;
            }
            return;
        }

        // Controlla per i commenti
        if (currentPosition + 3 < endOffset && buffer.charAt(currentPosition) == '<' &&
                buffer.charAt(currentPosition + 1) == '#' && buffer.charAt(currentPosition + 2) == '-' &&
                buffer.charAt(currentPosition + 3) == '-') {
            currentPosition += 4;
            currentToken = FreeMarkerTokenTypes.COMMENT_START;
            return;
        }

        // Controlla per la fine dei commenti
        if (currentPosition + 2 < endOffset && buffer.charAt(currentPosition) == '-' &&
                buffer.charAt(currentPosition + 1) == '-' && buffer.charAt(currentPosition + 2) == '>') {
            currentPosition += 3;
            currentToken = FreeMarkerTokenTypes.COMMENT_END;
            return;
        }

        // Controlla per le interpolazioni
        if (currentPosition + 1 < endOffset && buffer.charAt(currentPosition) == '$' && buffer.charAt(currentPosition + 1) == '{') {
            currentPosition += 2;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_START;
            return;
        }

        // Controlla per la fine delle interpolazioni
        if (buffer.charAt(currentPosition) == '}') {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.INTERPOLATION_END;
            return;
        }

        // Controlla per stringhe
        if (buffer.charAt(currentPosition) == '"' || buffer.charAt(currentPosition) == '\'') {
            char quoteChar = buffer.charAt(currentPosition);
            currentPosition++;
            while (currentPosition < endOffset && buffer.charAt(currentPosition) != quoteChar) {
                if (buffer.charAt(currentPosition) == '\\' && currentPosition + 1 < endOffset) {
                    currentPosition += 2; // Salta il carattere di escape e il carattere escaped
                } else {
                    currentPosition++;
                }
            }
            if (currentPosition < endOffset) {
                currentPosition++; // Salta la virgoletta di chiusura
            }
            currentToken = FreeMarkerTokenTypes.STRING;
            return;
        }

        // Controlla per numeri
        if (Character.isDigit(buffer.charAt(currentPosition))) {
            while (currentPosition < endOffset && (Character.isDigit(buffer.charAt(currentPosition)) ||
                    buffer.charAt(currentPosition) == '.')) {
                currentPosition++;
            }
            currentToken = FreeMarkerTokenTypes.NUMBER;
            return;
        }

        // Controlla per inizio tag HTML (ma non FreeMarker)
        if (buffer.charAt(currentPosition) == '<' && state == NORMAL) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.HTML_TAG_START;
            state = IN_HTML_TAG;
            return;
        }

        // Contenuto di tag HTML
        if (state == IN_HTML_TAG) {
            while (currentPosition < endOffset && buffer.charAt(currentPosition) != '>') {
                currentPosition++;
            }
            // Se siamo arrivati alla fine del buffer o abbiamo trovato '>'
            if (currentPosition > startOffset) {
                currentToken = FreeMarkerTokenTypes.HTML_TAG_CONTENT;
                return;
            }
        }


        // Controlla per parole chiave e identificatori
        if (currentPosition < endOffset && (Character.isLetter(buffer.charAt(currentPosition)) ||
                buffer.charAt(currentPosition) == '_')) {
            int start = currentPosition;
            while (currentPosition < endOffset && (Character.isLetterOrDigit(buffer.charAt(currentPosition)) ||
                    buffer.charAt(currentPosition) == '_')) {
                currentPosition++;
            }

            String word = buffer.subSequence(start, currentPosition).toString();
            // Lista di parole chiave FreeMarker
            if (word.equals("if") || word.equals("else") || word.equals("elseif") ||
                    word.equals("list") || word.equals("assign") || word.equals("include") ||
                    word.equals("import") || word.equals("macro") || word.equals("function") ||
                    word.equals("return") || word.equals("switch") || word.equals("case") ||
                    word.equals("default")) {
                currentToken = FreeMarkerTokenTypes.KEYWORD;
            } else {
                currentToken = FreeMarkerTokenTypes.IDENTIFIER;
            }
            return;
        }

        // Operatori
        if (currentPosition < endOffset && "+-*/=<>!&|^%".indexOf(buffer.charAt(currentPosition)) >= 0) {
            currentPosition++;
            currentToken = FreeMarkerTokenTypes.OPERATOR;
            return;
        }

        // Testo normale
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
}