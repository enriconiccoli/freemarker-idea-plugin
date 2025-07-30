package com.ennic;

import com.ennic.filetype.FreeMarkerFile;
import com.ennic.filetype.FreeMarkerLanguage;
import com.ennic.lexer.FreeMarkerLexer;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class FreeMarkerParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(FreeMarkerLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FreeMarkerLexer();
    }

    @Override
    public PsiParser createParser(Project project) {
        return (root, builder) -> {
            PsiBuilder.Marker rootMarker = builder.mark();
            while (!builder.eof()) {
                // Consuma tutti i token
                builder.advanceLexer();
            }
            rootMarker.done(root);
            return builder.getTreeBuilt(); // Aggiunto il return statement mancante
        };
    }
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return new FreeMarkerPsiElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(FileViewProvider viewProvider) {
        return new FreeMarkerFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
