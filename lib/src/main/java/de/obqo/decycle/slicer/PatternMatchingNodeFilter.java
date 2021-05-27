package de.obqo.decycle.slicer;

import de.obqo.decycle.model.Node;
import de.obqo.decycle.model.NodeFilter;

/**
 * {@link NodeFilter} that filters a {@link Node} based on its name.
 * <p>
 * A {@link Node} passes this filter it its name matches a given ant-like pattern.
 * <ul>
 *     <li>
 *         <code>*</code> matches a single (simple) class name or one part of package name (i.e. everything that is
 *         not a dot '.').<br>
 *         For example <code>java.util.*</code> will match all class names in the <code>java.util</code> package,
 *         but not in any subpackages (like <code>java.util.regex</code>).
 *         <code>java.util.*.*</code> will match all class names in all subpackages of <code>java.util</code>,
 *         but not the classes in <code>java.util</code>
 *     </li>
 *     <li>
 *         <code>**</code> matches any string including dots.<br>
 *         For example <code>java.util.**</code> will match all class names in the <code>java.util</code> package and
 *         its subpackages.
 *     </li>
 *     <li>
 *         The pattern may contain parentheses and the pipe symbol '|' to create more complex patterns.
 *     </li>
 * </ul>
 */
public class PatternMatchingNodeFilter implements NodeFilter {

    private final PatternMatcher matcher;

    public PatternMatchingNodeFilter(final String pattern) {
        this.matcher = new PatternMatcher(pattern);
    }

    @Override
    public boolean test(final Node node) {
        return this.matcher.matches(node.getName()).isPresent();
    }
}
