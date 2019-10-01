package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromDescriptor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.SimpleNode;

class GraphBuildingFieldVisitor extends FieldVisitor {

    private final Graph graph;
    private final SimpleNode currentClass;

    GraphBuildingFieldVisitor(final int api, final Graph graph, final SimpleNode currentClass) {
        super(api);
        this.graph = graph;
        this.currentClass = currentClass;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath,
                                                 final String descriptor,
                                                 final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }
}
