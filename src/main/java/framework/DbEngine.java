/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package framework;

/**
 *
 * @author Djurkovic
 */
public enum DbEngine {
    MYSQL("MySQL"),
    POSTGRES("PostgreSQL"),
    SQL_SERVER("Microsoft SQL Server");

    private final String display;

    DbEngine(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }

}
