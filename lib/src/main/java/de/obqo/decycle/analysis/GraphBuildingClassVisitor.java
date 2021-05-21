package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.GraphBuilder.classNodeFromTypeName;

import de.obqo.decycle.graph.Graph;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class GraphBuildingClassVisitor extends ClassVisitor {

    private final GraphBuilder graphBuilder;

    GraphBuildingClassVisitor(final Graph graph) {
        super(Opcodes.ASM9);
        this.graphBuilder = new GraphBuilder(graph);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
            final String superName, final String[] interfaces) {
        this.graphBuilder.visitCurrentClass(name);
        this.graphBuilder.connectNodesFromDescriptors(signature);

        if (superName != null) {
            this.graphBuilder.connect(classNodeFromTypeName(superName));
        }

        for (final String i : interfaces) {
            this.graphBuilder.connect(classNodeFromTypeName(i));
        }
    }

    @Override
    public void visitOuterClass(final String owner, final String name, final String descriptor) {
        // we don't gather this dependency, as it is implicitly given
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        // we don't gather this dependency, as it is implicitly given
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return new GraphBuildingAnnotationVisitor(this.api, this.graphBuilder);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor,
            final boolean visible) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor);
        return new GraphBuildingAnnotationVisitor(this.api, this.graphBuilder);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature,
            final Object value) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor, signature);
        return new GraphBuildingFieldVisitor(this.api, this.graphBuilder);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor,
            final String signature, final String[] exceptions) {
        this.graphBuilder.connectNodesFromDescriptors(descriptor, signature);
        if (exceptions != null) {
            for (final String e : exceptions) {
                this.graphBuilder.connect(classNodeFromTypeName(e));
            }
        }
        return new GraphBuildingMethodVisitor(this.api, this.graphBuilder);
    }
}
