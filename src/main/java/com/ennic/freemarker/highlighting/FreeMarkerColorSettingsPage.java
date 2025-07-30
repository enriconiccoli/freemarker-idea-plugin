package com.ennic.freemarker.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class FreeMarkerColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Directive", FreeMarkerSyntaxHighlighter.DIRECTIVE),
            new AttributesDescriptor("Interpolation", FreeMarkerSyntaxHighlighter.INTERPOLATION),
            new AttributesDescriptor("Comment", FreeMarkerSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("String", FreeMarkerSyntaxHighlighter.STRING),
            new AttributesDescriptor("Number", FreeMarkerSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Keyword", FreeMarkerSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Identifier", FreeMarkerSyntaxHighlighter.IDENTIFIER),
            new AttributesDescriptor("HTML Tag", FreeMarkerSyntaxHighlighter.HTML_TAG),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new FreeMarkerSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "<html>\n" +
                "  <body>\n" +
                "    <#directive param=\"value\">\n" +
                "    ${interpolation}\n" +
                "    <#-- commento -->\n" +
                "  </body>\n" +
                "</html>";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "FreeMarker";
    }
}