package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.ArrayMember;
import de.obqo.decycle.demo.base.to.BaseClass;
import de.obqo.decycle.demo.base.to.BaseInterface;
import de.obqo.decycle.demo.base.to.CheckedException;
import de.obqo.decycle.demo.base.to.ClassObject;
import de.obqo.decycle.demo.base.to.ConstructorAnnotation;
import de.obqo.decycle.demo.base.to.ConstructorParam;
import de.obqo.decycle.demo.base.to.FieldAnnotation;
import de.obqo.decycle.demo.base.to.GenericTypeForClass;
import de.obqo.decycle.demo.base.to.GenericTypeForList;
import de.obqo.decycle.demo.base.to.InstanceOfType;
import de.obqo.decycle.demo.base.to.LambdaExpression;
import de.obqo.decycle.demo.base.to.LocalTypeAnnotation;
import de.obqo.decycle.demo.base.to.LocalVar;
import de.obqo.decycle.demo.base.to.LocalVarAnnotation;
import de.obqo.decycle.demo.base.to.MethodAnnotation;
import de.obqo.decycle.demo.base.to.MethodParam;
import de.obqo.decycle.demo.base.to.MethodReference;
import de.obqo.decycle.demo.base.to.NonStaticConcreteMember;
import de.obqo.decycle.demo.base.to.NonStaticInitializer;
import de.obqo.decycle.demo.base.to.NonStaticMember;
import de.obqo.decycle.demo.base.to.ParamAnnotation;
import de.obqo.decycle.demo.base.to.ResourcesClassFactory;
import de.obqo.decycle.demo.base.to.ReturnType;
import de.obqo.decycle.demo.base.to.StaticConcreteMember;
import de.obqo.decycle.demo.base.to.StaticConstant;
import de.obqo.decycle.demo.base.to.StaticInitializer;
import de.obqo.decycle.demo.base.to.StaticMember;
import de.obqo.decycle.demo.base.to.StaticMethodParam;
import de.obqo.decycle.demo.base.to.TypeAnnotation;
import de.obqo.decycle.demo.base.to.TypeAnnotationParam;
import de.obqo.decycle.demo.base.to.TypeCast;
import de.obqo.decycle.demo.base.to.TypeParamAnnotation;
import de.obqo.decycle.demo.base.to.TypeUseAnnotation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TypeAnnotation(TypeAnnotationParam.X)
public class FromClass<@TypeParamAnnotation T extends GenericTypeForClass> extends BaseClass
        implements BaseInterface<T>, FromInterface<T> {

    @FieldAnnotation
    private static StaticMember staticMember = new StaticConcreteMember();

    @FieldAnnotation
    private NonStaticMember nonStaticMember = new NonStaticConcreteMember();

    private List<@TypeUseAnnotation GenericTypeForList> list;

    private Map<Class<?>, Integer> classes = Map.of(ClassObject.class, 0);

    private ArrayMember[] array;

    // not reported (constant has been inlined by the compiler)
    private String s = StaticConstant.CONSTANT_STRING;

    static {
        Object o = new StaticInitializer();
        o.toString();
        staticMethod(new StaticMethodParam());
    }

    public static final FromClass<GenericTypeForClass> INSTANCE = new FromClass<>(new ConstructorParam());

    {
        NonStaticInitializer i = Optional.of("foo").map(s -> LambdaExpression.create(s, 0)).orElse(null);
    }

    @ConstructorAnnotation
    public FromClass(ConstructorParam param) {
        final Function<String, GenericTypeForList> methodRef = MethodReference::ref;
        this.list = Stream.of("one", "two", "three").map(methodRef).collect(Collectors.toList());
    }

    static Object staticMethod(@ParamAnnotation StaticMethodParam param) {
        // not reported, see https://docs.oracle.com/javase/specs/jls/se14/html/jls-9.html#jls-9.6.4.2
        @LocalVarAnnotation
        LocalVar l = new LocalVar();
        return l;
    }

    @MethodAnnotation
    public ReturnType method(MethodParam param) throws CheckedException {

        if (param == null) {
            throw new CheckedException();
        }

        @LocalTypeAnnotation
        class LocalClass extends ReturnType { // expands to FromClass$1LocalClass

            void localClassMethod() {
                var x = new BaseInterface<@TypeUseAnnotation Integer>() { // expands to FromClass$1LocalClass$1

                };
            }
        }

        return new LocalClass();
    }

    public void otherMethod(Object o) {
        if (o instanceof InstanceOfType) {
            var x = (TypeCast) o;

            System.out.println(x);
        }
    }

    public void resourcesMethod() {
        try (var resource = ResourcesClassFactory.open()) {
            System.out.println(resource.hashCode());
        }
    }

}
