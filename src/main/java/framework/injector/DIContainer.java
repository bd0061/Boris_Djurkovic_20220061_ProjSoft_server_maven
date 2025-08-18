package framework.injector;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DIContainer {

    private final Map<Type, Supplier<?>> mapiranje = new HashMap<>();

    public <T> void register(Type interfaceType, Supplier<? extends T> supplier) {
        mapiranje.put(interfaceType, supplier);
    }

    public <T> T resolve(Type interfaceType) {
        Supplier<?> supplier = mapiranje.get(interfaceType);
        if(supplier == null) {
            throw new RuntimeException("Nije dodeljen provider tipu: " + interfaceType);
        }
        return (T) supplier.get();
    }

}
