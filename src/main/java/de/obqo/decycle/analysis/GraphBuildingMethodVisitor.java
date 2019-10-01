package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.VisitorSupport.classNode;
import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromDescriptor;
import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromSingleType;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.SimpleNode;

class GraphBuildingMethodVisitor extends MethodVisitor {

    private final Graph graph;
    private final SimpleNode currentClass;

    GraphBuildingMethodVisitor(final int api, final Graph graph, final SimpleNode currentClass) {
        super(api);
        this.graph = graph;
        this.currentClass = currentClass;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath,
                                                 final String descriptor,
                                                 final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor,
                                                      final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack,
                           final Object[] stack) {
        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        graph.connect(currentClass, classNodeFromSingleType(type));
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        graph.connect(currentClass, classNodeFromSingleType(owner));
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor,
                                final boolean isInterface) {
        graph.connect(currentClass, classNodeFromSingleType(owner));
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String descriptor,
                                       final Handle bootstrapMethodHandle,
                                       final Object... bootstrapMethodArguments) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public void visitLdcInsn(final Object value) {
        if (value instanceof Type) {
            classNodeFromDescriptor(((Type) value).getDescriptor())
                    .forEach(node -> graph.connect(currentClass, node));
        }
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        // nothing to do
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath,
                                                 final String descriptor,
                                                 final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        if (type != null) {
            graph.connect(currentClass, classNode(type));
        }
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(final int typeRef, final TypePath typePath,
                                                     final String descriptor,
                                                     final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }

    @Override
    public void visitLocalVariable(final String name, final String descriptor, final String signature,
                                   final Label start, final Label end,
                                   final int index) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        classNodeFromDescriptor(signature).forEach(node -> graph.connect(currentClass, node));
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(final int typeRef, final TypePath typePath,
                                                          final Label[] start,
                                                          final Label[] end, final int[] index, final String descriptor, final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> graph.connect(currentClass, node));
        return new GraphBuildingAnnotationVisitor(api, graph, currentClass);
    }
}
