package com.ennic.freemarker.lexer;

import com.intellij.psi.tree.IElementType;

public interface FreeMarkerTokenTypes {
    IElementType TEXT = new FreeMarkerElementType("TEXT");
    IElementType DIRECTIVE_START = new FreeMarkerElementType("DIRECTIVE_START"); // <#
    IElementType DIRECTIVE_CLOSING = new FreeMarkerElementType("DIRECTIVE_CLOSING"); // </#
    IElementType DIRECTIVE_END = new FreeMarkerElementType("DIRECTIVE_END"); // >
    IElementType INTERPOLATION_START = new FreeMarkerElementType("INTERPOLATION_START"); // ${
    IElementType INTERPOLATION_END = new FreeMarkerElementType("INTERPOLATION_END"); // }
    IElementType COMMENT_START = new FreeMarkerElementType("COMMENT_START"); // <#--
    IElementType COMMENT_END = new FreeMarkerElementType("COMMENT_END"); // -->
    IElementType COMMENT_TODO = new FreeMarkerElementType("COMMENT_TODO"); // Commenti che iniziano con "todo"
    IElementType IDENTIFIER = new FreeMarkerElementType("IDENTIFIER");
    IElementType STRING = new FreeMarkerElementType("STRING");
    IElementType NUMBER = new FreeMarkerElementType("NUMBER");
    IElementType KEYWORD = new FreeMarkerElementType("KEYWORD");
    IElementType OPERATOR = new FreeMarkerElementType("OPERATOR");
    IElementType HTML_TAG_START = new FreeMarkerElementType("HTML_TAG_START");
    IElementType HTML_TAG_END = new FreeMarkerElementType("HTML_TAG_END");
    IElementType HTML_TAG_CONTENT = new FreeMarkerElementType("HTML_TAG_CONTENT");
}
