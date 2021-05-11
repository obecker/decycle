package de.obqo.decycle.demo.base.to;

import java.util.List;

public interface GenericMethod<T, R> {

    List<? super R> execute(List<? extends T> t);

}
