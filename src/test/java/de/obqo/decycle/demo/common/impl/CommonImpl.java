package de.obqo.decycle.demo.common.impl;

import de.obqo.decycle.demo.common.CommonAnno;
import de.obqo.decycle.demo.common.api.CommonApi;
import de.obqo.decycle.demo.util.Generator;

@CommonAnno
public class CommonImpl implements CommonApi {

    private static int number = Generator.generateNumber();

}
