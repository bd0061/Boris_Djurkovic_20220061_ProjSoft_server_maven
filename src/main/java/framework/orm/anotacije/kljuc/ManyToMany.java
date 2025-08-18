/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/AnnotationType.java to edit this template
 */
package framework.orm.anotacije.kljuc;

/**
 *
 * @author Djurkovic
 */
public @interface ManyToMany {
    String junctionTable();
    String joinColumn();
    String inverseJoinColumn();
}
