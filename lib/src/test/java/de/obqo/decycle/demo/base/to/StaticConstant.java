package de.obqo.decycle.demo.base.to;

public class StaticConstant {

    public static final String CONSTANT_STRING = "a constant value"; // not reported (inlined)

    public static final Object CONSTANT_OBJECT = new Object(); // reported

}
