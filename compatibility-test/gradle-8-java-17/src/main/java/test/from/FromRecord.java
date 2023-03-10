package test.from;

import test.to.ToAnnotation;
import test.to.ToClass;

public record FromRecord(@ToAnnotation ToClass toClass) {
}
