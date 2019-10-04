package de.obqo.decycle.analysis;

import static de.obqo.decycle.analysis.VisitorSupport.classNode;
import static de.obqo.decycle.analysis.VisitorSupport.classNodeFromDescriptor;

import de.obqo.decycle.graph.Graph;
import de.obqo.decycle.model.SimpleNode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class GraphBuildingClassVisitor extends ClassVisitor {

    private final Graph graph;
    private SimpleNode currentClass;

    GraphBuildingClassVisitor(Graph graph) {
        super(Opcodes.ASM7);
        this.graph = graph;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
            final String superName, final String[] interfaces) {

        this.currentClass = classNode(name);

        classNodeFromDescriptor(signature).forEach(node -> this.graph.connect(this.currentClass, node));

        if (superName == null) {
            this.graph.add(this.currentClass);
        } else {
            this.graph.connect(this.currentClass, classNode(superName));
        }

        for (String i : interfaces) {
            this.graph.connect(this.currentClass, classNode(i));
        }
    }

    @Override
    public ModuleVisitor visitModule(final String name, final int access, final String version) {
        // TODO
        return super.visitModule(name, access, version);
    }

    @Override
    public void visitOuterClass(final String owner, final String name, final String descriptor) {
        this.graph.add(classNode(owner));
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.add(node));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return new GraphBuildingAnnotationVisitor(this.api, this.graph, this.currentClass);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor,
            final boolean visible) {
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return new GraphBuildingAnnotationVisitor(this.api, this.graph, this.currentClass);
    }

    @Override
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        if (outerName == null) { // what the heck does that mean?
            this.graph.add(classNode(name));
        } else {
            // we don't gather this dependency, but deduce it through the naming ... not sure how good that idea is.
            // graph.connect(classNode(name), classNode(outerName));
        }
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature,
            final Object value) {
        classNodeFromDescriptor(signature).forEach(node -> this.graph.connect(this.currentClass, node));
        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        return new GraphBuildingFieldVisitor(this.api, this.graph, this.currentClass);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor,
            final String signature,
            final String[] exceptions) {
        classNodeFromDescriptor(signature).forEach(node -> this.graph.connect(this.currentClass, node));

        classNodeFromDescriptor(descriptor).forEach(node -> this.graph.connect(this.currentClass, node));
        if (exceptions != null) {
            for (String e : exceptions) {
                classNodeFromDescriptor(e).forEach(node -> this.graph.connect(this.currentClass, node));
            }
        }
        return new GraphBuildingMethodVisitor(this.api, this.graph, this.currentClass);
    }
}
