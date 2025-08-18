/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package iznajmljivanjeapp.domain.enumeracije;

/**
 *
 * @author Djurkovic
 */
public enum TipTerminaEnum {
    PREPODNE,
    POPODNE,
    NOC;

    public static TipTerminaEnum fromInt(int index) {
        if (index < 0 || index >= TipTerminaEnum.values().length) {
            throw new IllegalArgumentException("Invalid index for TipTerminaEnum: " + index);
        }
        return TipTerminaEnum.values()[index];
    }
}
