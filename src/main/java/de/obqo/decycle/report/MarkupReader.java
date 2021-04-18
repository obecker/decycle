package de.obqo.decycle.report;

import static j2html.TagCreator.rawHtml;
import static j2html.tags.InlineStaticResource.getFileAsString;

import java.util.Scanner;

import j2html.tags.DomContent;

class MarkupReader {

    static DomContent inlineMarkup(final String path) {
        final String contentAsString = getFileAsString(path);
        return rawHtml(contentAsString);
    }

    static DomContent inlineMarkup_min(final String path) {
        final String contentAsString = getFileAsString(path);
        return rawHtml(minifyMarkup(contentAsString));
    }

    private static String minifyMarkup(final String input) {
        // very basic markup minifier: trim all lines and remove empty lines
        final Scanner scanner = new Scanner(input);
        final StringBuilder builder = new StringBuilder();

        String line;
        while(scanner.hasNext()) {
            line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) != '<') { // line break within tag or attribute
                builder.append(' ');
            }
            builder.append(line);
        }
        return builder.toString();
    }
}
