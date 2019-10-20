package de.obqo.decycle.analysis;

import de.obqo.decycle.model.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class VisitorSupport {

    private static Pattern singlePattern = Pattern.compile("\\[*L([\\w/$]+);");
    private static Pattern multiPattern = Pattern.compile("(?<=L)([\\w/$]+)(?=[;<])");

    static Node classNode(final String slashSeparatedName) {
        return Node.classNode(slashSeparatedName.replace('/', '.'));
    }

    static Node classNodeFromSingleType(final String singleTypeDescription) {
        final Matcher matcher = singlePattern.matcher(singleTypeDescription);
        if (matcher.matches()) {
            return classNode(matcher.group(1));
        } else {
            return classNode(singleTypeDescription);
        }
    }

    static Set<Node> classNodeFromDescriptor(final String desc) {
        if (desc == null || desc.isEmpty()) {
            return Set.of();
        } else {
            final Set<Node> result = new HashSet<>();
            final Matcher matcher = multiPattern.matcher(desc);
            while (matcher.find()) {
                result.add(classNode(desc.substring(matcher.start(), matcher.end())));
            }
            return result;
        }
    }

}
