package com.ennic.freemarker.filetype;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
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
        return IconLoader.getIcon("/icons/ftlIcon.png", getClass());
    }
}
