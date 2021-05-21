package de.obqo.decycle.demo.base.from;

import de.obqo.decycle.demo.base.to.BaseInterface;
import de.obqo.decycle.demo.base.to.CheckedException;
import de.obqo.decycle.demo.base.to.GenericTypeForClass;
import de.obqo.decycle.demo.base.to.MethodParam;
import de.obqo.decycle.demo.base.to.ReturnType;
import de.obqo.decycle.demo.base.to.StaticConcreteMember;
import de.obqo.decycle.demo.base.to.StaticMember;

public interface FromInterface<T extends GenericTypeForClass> extends BaseInterface<T> {

    StaticMember member = new StaticConcreteMember();

    ReturnType method(MethodParam param) throws CheckedException;
}
