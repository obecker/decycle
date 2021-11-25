package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.CatchAnnotation;
import de.obqo.decycle.demo.base.to.CheckedException;
import de.obqo.decycle.demo.base.to.EllipsisParam;
import de.obqo.decycle.demo.base.to.GenericMethod;
import de.obqo.decycle.demo.base.to.GenericTypeForClass;
import de.obqo.decycle.demo.base.to.GenericTypeForList;
import de.obqo.decycle.demo.base.to.InCatchBlock;
import de.obqo.decycle.demo.base.to.InFinally;
import de.obqo.decycle.demo.base.to.InTry;
import de.obqo.decycle.demo.base.to.MethodParam;
import de.obqo.decycle.demo.base.to.ReturnType;
import de.obqo.decycle.demo.base.to.StaticConstant;
import de.obqo.decycle.demo.base.to.UncheckedException1;
import de.obqo.decycle.demo.base.to.UncheckedException2;

import java.util.List;

public class FromOtherClass {

    private final FromClass<?> delegate;

    private static GenericMethod<GenericTypeForClass, GenericTypeForList> g = c -> List.of(new GenericTypeForList());

    private Object o = StaticConstant.CONSTANT_OBJECT;

    public FromOtherClass(final FromClass<?> delegate) {
        this.delegate = delegate;
    }

    public ReturnType delegateMethod(final MethodParam param) throws CheckedException {
        try {
            System.out.println("Try ...");
            InTry.doIt(EllipsisParam.of("un"), EllipsisParam.of("deux"));
            return this.delegate.method(param);
        } catch (@CatchAnnotation final IllegalStateException exception) { // TODO CatchAnnotation not reported
            System.err.println("Argh! " + exception.getMessage());
        } catch (final UncheckedException1 | UncheckedException2 exception) {
            System.err.println("Doh! " + exception.getMessage());
            new InCatchBlock();
        } finally {
            final String name = InFinally.class.getName();
        }
        return null;
    }
}
