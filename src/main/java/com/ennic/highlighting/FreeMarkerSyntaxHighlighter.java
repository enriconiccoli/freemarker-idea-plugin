package com.ennic.highlighting;

import com.ennic.lexer.FreeMarkerLexer;
import com.ennic.lexer.FreeMarkerTokenTypes;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class FreeMarkerSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey DIRECTIVE =
            createTextAttributesKey("FREEMARKER_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey INTERPOLATION =
            createTextAttributesKey("FREEMARKER_INTERPOLATION", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("FREEMARKER_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("FREEMARKER_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("FREEMARKER_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("FREEMARKER_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("FREEMARKER_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey HTML_TAG =
            createTextAttributesKey("FREEMARKER_HTML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);

    private static final TextAttributesKey[] DIRECTIVE_KEYS = new TextAttributesKey[]{DIRECTIVE};
    private static final TextAttributesKey[] INTERPOLATION_KEYS = new TextAttributesKey[]{INTERPOLATION};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
    private static final TextAttributesKey[] HTML_TAG_KEYS = new TextAttributesKey[]{HTML_TAG};

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FreeMarkerLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(FreeMarkerTokenTypes.DIRECTIVE_START) ||
                tokenType.equals(FreeMarkerTokenTypes.DIRECTIVE_END) || tokenType.equals(FreeMarkerTokenTypes.DIRECTIVE_CLOSING)) {
            return DIRECTIVE_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.HTML_TAG_START) ||
                tokenType.equals(FreeMarkerTokenTypes.HTML_TAG_END) ||
                tokenType.equals(FreeMarkerTokenTypes.HTML_TAG_CONTENT)) {
            return HTML_TAG_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.INTERPOLATION_START) ||
                tokenType.equals(FreeMarkerTokenTypes.INTERPOLATION_END)) {
            return INTERPOLATION_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.COMMENT_START) ||
                tokenType.equals(FreeMarkerTokenTypes.COMMENT_END)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.NUMBER)) {
            return NUMBER_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.KEYWORD)) {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(FreeMarkerTokenTypes.IDENTIFIER)) {
            return IDENTIFIER_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}