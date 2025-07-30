package com.ennic.filetype;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class FreeMarkerFile extends PsiFileBase {
    public FreeMarkerFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FreeMarkerLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FreeMarkerFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FreeMarker File";
    }
}
