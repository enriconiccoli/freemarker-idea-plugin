package com.ennic.lexer;

import com.ennic.filetype.FreeMarkerLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class FreeMarkerElementType extends IElementType {
    public FreeMarkerElementType(@NotNull @NonNls String debugName) {
        super(debugName, FreeMarkerLanguage.INSTANCE);
    }
}