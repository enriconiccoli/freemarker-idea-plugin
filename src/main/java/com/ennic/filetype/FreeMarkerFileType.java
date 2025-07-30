package com.ennic.filetype;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FreeMarkerFileType extends LanguageFileType {
    public static final FreeMarkerFileType INSTANCE = new FreeMarkerFileType();

    private FreeMarkerFileType() {
        super(FreeMarkerLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "FreeMarker File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "FreeMarker template file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ftl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null; // puoi aggiungere un'icona personalizzata qui
    }
}
