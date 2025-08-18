/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package framework.orm;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 *
 * @author Djurkovic
 */

//pravimo nas consumer jer refleksija baca izuzetke...
@FunctionalInterface
public interface MojConsumer {
    void accept(Field field, Object value) throws IllegalAccessException,SQLException;
}
