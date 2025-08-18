/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/AnnotationType.java to edit this template
 */
package framework.orm.anotacije.kljuc;

import java.lang.annotation.*;

/**
 *
 * @author Djurkovic
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimarniKljuc {
    
}
