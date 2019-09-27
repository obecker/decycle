package de.obqo.decycle.analyze;

import static de.obqo.decycle.analyze.VisitorSupport.classNodeFromDescriptor;
import static de.obqo.decycle.analyze.VisitorSupport.classNodeFromSingleType;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.SimpleNode;

class GraphBuildingAnnotationVisitor extends AnnotationVisitor {

    private final Graph graph;
    private final SimpleNode currentClass;

    GraphBuildingAnnotationVisitor(final int api, final Graph graph, final SimpleNode currentClass) {
        super(api);
        this.graph = graph;
        this.currentClass = currentClass;
    }

    @Override
    public void visit(final String name, final Object value) {
        if (value instanceof Type) {
            graph.connect(currentClass, classNodeFromSingleType(((Type) value).getClassName()));
        }
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return this;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return this;
    }
}
