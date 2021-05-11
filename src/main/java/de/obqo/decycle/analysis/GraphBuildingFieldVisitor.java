package de.obqo.decycle.analysis;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

class GraphBuildingFieldVisitor extends FieldVisitor {

    private final GraphBuilder graphBuilder;

    GraphBuildingFieldVisitor(final int api, final GraphBuilder graphBuilder) {
        super(api);
        this.graphBuilder = graphBuilder;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return new GraphBuildingAnnotationVisitor(this.api, this.graphBuilder);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath,
            final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return new GraphBuildingAnnotationVisitor(this.api, this.graphBuilder);
    }
}
