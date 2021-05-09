package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.AnnotationAnnotation;
import de.obqo.decycle.demo.base.to.TypeAnnotationParam;

@AnnotationAnnotation
public @interface FromAnnotation {

    TypeAnnotationParam value() default TypeAnnotationParam.Y;
}
