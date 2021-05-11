package de.obqo.decycle.analysis;

import org.objectweb.asm.AnnotationVisitor;

class GraphBuildingAnnotationVisitor extends AnnotationVisitor {

    private final GraphBuilder graphBuilder;

    GraphBuildingAnnotationVisitor(final int api, final GraphBuilder graphBuilder) {
        super(api);
        this.graphBuilder = graphBuilder;
    }

    @Override
    public void visit(final String name, final Object value) {
        this.graphBuilder.connectValue(value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return this;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return this;
    }
}
