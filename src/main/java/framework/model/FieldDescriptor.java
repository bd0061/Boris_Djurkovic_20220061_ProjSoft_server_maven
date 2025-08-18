package framework.model;

import java.lang.reflect.Field;

public class FieldDescriptor {
    public String clazz;
    public String fieldName;

    public Field get() throws NoSuchFieldException, ClassNotFoundException {
        return Class.forName(clazz).getDeclaredField(fieldName);
    }

    public FieldDescriptor(String clazz, String fieldName) {
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    public FieldDescriptor() {
    }
}