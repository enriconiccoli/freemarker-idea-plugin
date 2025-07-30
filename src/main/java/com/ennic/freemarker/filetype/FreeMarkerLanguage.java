package com.ennic.freemarker.filetype;

import com.intellij.lang.Language;

public class FreeMarkerLanguage extends Language {
    public static final FreeMarkerLanguage INSTANCE = new FreeMarkerLanguage();

    private FreeMarkerLanguage() {
        super("FreeMarker");
    }
}
