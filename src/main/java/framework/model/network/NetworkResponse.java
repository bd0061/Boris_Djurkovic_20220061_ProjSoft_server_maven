/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.model.network;

import java.io.Serializable;

/**
 *
 * @author Djurkovic
 */
public class NetworkResponse implements Serializable {

    public NetworkResponse(String responseMessage, boolean success, Object payload) {
        this.responseMessage = responseMessage;
        this.success = success;
        this.payload = payload;
    }

    public NetworkResponse() {}

    public static NetworkResponse Neuspeh(String poruka) {
        return new NetworkResponse(poruka, false, null);
    }

    public static NetworkResponse Uspeh(String poruka, Object payload) {
        return new NetworkResponse(poruka, true, payload);
    }

    public static NetworkResponse Uspeh(String poruka) {
        return new NetworkResponse(poruka, true, null);
    }

    public String responseMessage;
    public boolean success;
    public Object payload;
}
