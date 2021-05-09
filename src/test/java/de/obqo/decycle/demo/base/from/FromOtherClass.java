package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.CatchAnnotation;
import de.obqo.decycle.demo.base.to.CheckedException;
import de.obqo.decycle.demo.base.to.MethodParam;
import de.obqo.decycle.demo.base.to.ReturnType;
import de.obqo.decycle.demo.base.to.UncheckedException1;
import de.obqo.decycle.demo.base.to.UncheckedException2;

public class FromOtherClass {

    private final FromClass delegate;

    public FromOtherClass(final FromClass delegate) {
        this.delegate = delegate;
    }

    public ReturnType delegateMethod(MethodParam param) throws CheckedException {
        try {
            System.out.println("Try ...");
            return delegate.method(param);
        } catch (@CatchAnnotation IllegalStateException exception) { // TODO CatchAnnotation not reported
            System.err.println("Argh! " + exception.getMessage());
        } catch (UncheckedException1 | UncheckedException2 exception) {
            System.err.println("Doh! " + exception.getMessage());
        }
        return null;
    }
}
