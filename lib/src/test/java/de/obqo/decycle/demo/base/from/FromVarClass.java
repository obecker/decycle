package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.FieldAnnotation;
import de.obqo.decycle.demo.base.to.GenericMethod;
import de.obqo.decycle.demo.base.to.HierarchyA;
import de.obqo.decycle.demo.base.to.HierarchyB;
import de.obqo.decycle.demo.base.to.HierarchyC;
import de.obqo.decycle.demo.base.to.ReturnTypeFactory;

import java.util.List;

public class FromVarClass {

    @FieldAnnotation(params = {
            @FieldAnnotation.FieldAnnotationParam("first"),
            @FieldAnnotation.FieldAnnotationParam("second") })
    private ReturnTypeFactory factory;

    void check() {
        var value = this.factory.getValue();
    }

    private void bar() {
        GenericMethod<HierarchyB, HierarchyB> theMethod = a -> List.of(new HierarchyA());
        List<HierarchyC> cs = List.of(new HierarchyC());
        var result = theMethod.execute(cs);
        System.out.println(result);
    }
}
