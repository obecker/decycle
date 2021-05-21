package demo.cycle.b;

import demo.cycle.a.A2;
import demo.cycle.c.C;

public class B {

    private static A2 a = new A2();
    private static C c = new C();

    public static class InnerB {}

}
