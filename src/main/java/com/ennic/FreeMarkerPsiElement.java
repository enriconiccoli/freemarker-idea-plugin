package com.ennic;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class FreeMarkerPsiElement extends ASTWrapperPsiElement {
    public FreeMarkerPsiElement(@NotNull ASTNode node) {
        super(node);
    }
}
