package de.obqo.decycle.demo.common;

import de.obqo.decycle.demo.common.api.CommonApi;
import de.obqo.decycle.demo.common.impl.CommonImpl;

public class CommonFactory {

    public CommonApi getApi() {
        return new CommonImpl();
    }

}
