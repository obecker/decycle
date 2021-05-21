package de.obqo.decycle.demo.base.to;

import lombok.Value;

@Value
public class EllipsisParam {

    String value;

    public static EllipsisParam of(final String s) {
        return new EllipsisParam(s);
    }
}
