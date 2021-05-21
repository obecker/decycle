package de.obqo.decycle.demo.base.to;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface TypeAnnotation {

    TypeAnnotationParam value();
}
