package de.obqo.decycle.analysis;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class GraphBuilder {

    private static final Pattern singlePattern = Pattern.compile("\\[*L([\\w/$]+);");
    private static final Pattern multiPattern = Pattern.compile("(?<=L)([\\w/$]+)(?=[;<])");

    private final Graph graph;
    private Node currentNode;

    void visitCurrentClass(final String slashSeparatedName) {
        Preconditions.checkState(this.currentNode == null, "Can only visit one class per GraphBuilder");
        this.currentNode = classNodeFromTypeName(slashSeparatedName);
    }

    static Node classNodeFromTypeName(final String slashSeparatedName) {
        return Node.classNode(slashSeparatedName.replace('/', '.'));
    }

    static Node classNodeFromSingleType(final String singleTypeDescription) {
        final Matcher matcher = singlePattern.matcher(singleTypeDescription);
        if (matcher.matches()) {
            return classNodeFromTypeName(matcher.group(1));
        } else {
            return classNodeFromTypeName(singleTypeDescription);
        }
    }

    void connect(final Node node) {
        this.graph.connect(this.currentNode, node);
    }

    void connectNodesFromDescriptors(final String... descriptors) {
        classNodeFromDescriptors(descriptors).forEach(this::connect);
    }

    private Set<Node> classNodeFromDescriptors(final String... descriptors) {
        final Set<Node> result = new HashSet<>();
        for (final String descriptor : descriptors) {
            if (descriptor != null && !descriptor.isEmpty()) {
                final Matcher matcher = multiPattern.matcher(descriptor);
                while (matcher.find()) {
                    result.add(classNodeFromTypeName(matcher.group()));
                }
            }
        }
        return result;
    }

    void connectValue(final Object value) {
        if (value instanceof Type) {
            connectNodesFromDescriptors(((Type) value).getDescriptor());
        } else if (value instanceof Handle) {
            final Handle handle = (Handle) value;
            connect(classNodeFromTypeName(handle.getOwner()));
            connectNodesFromDescriptors(handle.getDesc());
        } else if (value instanceof ConstantDynamic) {
            final ConstantDynamic constantDynamic = (ConstantDynamic) value;
            connectNodesFromDescriptors(constantDynamic.getDescriptor());
            for (int i = 0; i < constantDynamic.getBootstrapMethodArgumentCount(); i++) {
                connectValue(constantDynamic.getBootstrapMethodArgument(i));
            }
        }
    }
}
