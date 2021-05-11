package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.EllipsisParam;
import de.obqo.decycle.demo.base.to.MethodAnnotation;
import de.obqo.decycle.demo.base.to.ToEnum;
import de.obqo.decycle.demo.base.to.TypeAnnotation;
import de.obqo.decycle.demo.base.to.TypeAnnotationParam;

@TypeAnnotation(TypeAnnotationParam.Y)
public enum FromEnum {

    A(EllipsisParam.of("a")), B(EllipsisParam.of("b1"), EllipsisParam.of("b2")), C;

    private final EllipsisParam[] params;

    FromEnum(final EllipsisParam... params) {
        this.params = params;
    }

    @MethodAnnotation
    void foo() {
        ToEnum to = ToEnum.Z;
    }
}
