package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromDescriptor;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.Node;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

class GraphBuildingFieldVisitor extends FieldVisitor {

    private final Graph graph;
    private final Node currentClass;

    GraphBuildingFieldVisitor(final int api, final Graph graph, final Node currentClass) {
        super(api);
        this.graph = graph;
        this.currentClass = currentClass;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return new GraphBuildingAnnotationVisitor(this.api, this.graph, this.currentClass);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath,
            final String descriptor,
            final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return new GraphBuildingAnnotationVisitor(this.api, this.graph, this.currentClass);
    }
}
