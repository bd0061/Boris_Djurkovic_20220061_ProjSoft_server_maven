/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.orm.util;

import java.lang.reflect.Method;

/**
 *
 * @author Djurkovic
 */
public class EnumConverter {

    public static <E extends Enum<E>> E fromOrdinal(Class<E> enumClass, int ordinal) {
        E[] constants = enumClass.getEnumConstants();
        if (ordinal < 0 || ordinal >= constants.length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return constants[ordinal];
    }
}
