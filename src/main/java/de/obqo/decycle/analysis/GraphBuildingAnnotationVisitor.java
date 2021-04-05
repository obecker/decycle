package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromDescriptor;
import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromSingleType;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

class GraphBuildingAnnotationVisitor extends AnnotationVisitor {

    private final Graph graph;
    private final Node currentClass;

    GraphBuildingAnnotationVisitor(final int api, final Graph graph, final Node currentClass) {
        super(api);
        this.graph = graph;
        this.currentClass = currentClass;
    }

    @Override
    public void visit(final String name, final Object value) {
        if (value instanceof Type) {
            this.graph.connect(this.currentClass, classNodeFromSingleType(((Type) value).getClassName()));
        }
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return this;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return this;
    }
}
