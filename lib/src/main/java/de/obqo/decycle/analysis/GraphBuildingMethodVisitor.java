package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.GraphBuilder.classNodeFromSingleType;
import static de.obqo.decycle.analysis.GraphBuilder.classNodeFromTypeName;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

class GraphBuildingMethodVisitor extends MethodVisitor {

    private final GraphBuilder graphBuilder;

    GraphBuildingMethodVisitor(final int api, final GraphBuilder graphBuilder) {
        super(api);
        this.graphBuilder = graphBuilder;
    }

    private GraphBuildingAnnotationVisitor annotationVisitor() {
        return new GraphBuildingAnnotationVisitor(this.api, this.graphBuilder);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return annotationVisitor();
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath,
            final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor,
            final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.graphBuilder.connect(classNodeFromSingleType(type));
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        this.graphBuilder.connect(classNodeFromSingleType(owner));
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor,
            final boolean isInterface) {
        this.graphBuilder.connect(classNodeFromSingleType(owner));
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String descriptor, final Handle bootstrapMethodHandle,
            final Object... bootstrapMethodArguments) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        for (final Object argument : bootstrapMethodArguments) {
            this.graphBuilder.connectValue(argument);
        }
    }

    @Override
    public void visitLdcInsn(final Object value) {
        this.graphBuilder.connectValue(value);
    }

    @Override
    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath, final String descriptor,
            final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        if (type != null) {
            this.graphBuilder.connect(classNodeFromTypeName(type));
        }
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(final int typeRef, final TypePath typePath,
            final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }

    @Override
    public void visitLocalVariable(final String name, final String descriptor, final String signature,
            final Label start, final Label end, final int index) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor, signature);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(final int typeRef, final TypePath typePath,
            final Label[] start, final Label[] end, final int[] index, final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return annotationVisitor();
    }
}
