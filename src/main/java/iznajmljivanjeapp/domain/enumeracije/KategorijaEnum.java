/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package iznajmljivanjeapp.domain.enumeracije;

/**
 *
 * @author Djurkovic
 */
public enum KategorijaEnum {
    BUDZET,
    SREDNJA,
    LUKSUZ;

    public static KategorijaEnum fromInt(int index) {
        if (index < 0 || index >= KategorijaEnum.values().length) {
            throw new IllegalArgumentException("Invalid index for KategorijaEnum: " + index);
        }
        return KategorijaEnum.values()[index];
    }
}
