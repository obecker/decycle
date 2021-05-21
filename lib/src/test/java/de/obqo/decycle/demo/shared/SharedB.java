package de.obqo.decycle.demo.shared;

import de.obqo.decycle.demo.helper.StringHelper;

import java.util.Comparator;

import lombok.Value;

public class SharedB {

    @Value
    private static class Wrapper {

        int value;
    }

    public void run() {
        Wrapper a = new Wrapper(7);
        Wrapper b = new Wrapper(13);

        Comparator<Wrapper> c = Comparator.comparing(x -> StringHelper.toRepeatedString(x.value));
    }

}
