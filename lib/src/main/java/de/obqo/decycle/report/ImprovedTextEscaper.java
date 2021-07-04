package de.obqo.decycle.report;

/**
 * {@link j2html.utils.TextEscaper} implementation that prevents constructing a new {@link String} object if there's no
 * character that needs to be escaped (unlike the default {@link j2html.utils.EscapeUtil}).
 */
class ImprovedTextEscaper {

    static String escape(final String s) {
        int lastIndex = 0;
        StringBuilder escapedText = null;
        String replacement;
        loop:
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
            case '<':
                replacement = "&lt;";
                break;
            case '>':
                replacement = "&gt;";
                break;
            case '&':
                replacement = "&amp;";
                break;
            case '"':
                replacement = "&quot;";
                break;
// Apostrophe doesn't need to be escaped since attributes are always enclosed in quotes
//            case '\'':
//                replacement = "&apos;";
//                break;
            default:
                continue loop;
            }
            if (escapedText == null) {
                escapedText = new StringBuilder(s.length() + 16);
            }
            escapedText.append(s, lastIndex, i).append(replacement);
            lastIndex = i + 1;
        }
        return escapedText != null ? escapedText.append(s, lastIndex, s.length()).toString() : s;
    }
}
