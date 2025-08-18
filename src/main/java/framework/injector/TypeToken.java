package framework.injector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {
    private Type type;

    public TypeToken() {
        Type t = getClass().getGenericSuperclass();
        if(t instanceof ParameterizedType pt) {
            type = pt.getActualTypeArguments()[0];
        }
        else {
            throw new RuntimeException("Los type token: tip nije parametrizovan");
        }
    }

    public Type getType() {
        return type;
    }

}
