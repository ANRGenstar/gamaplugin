package gama.genstar.plugin;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;


@SuppressWarnings({"rawtypes", "unchecked"})
public class Foo {

    @operator(
            value = "faitrien"
    )
    @doc(value = "bla")
    public static String faitrien(String arg) {
        return arg;
    }
}
