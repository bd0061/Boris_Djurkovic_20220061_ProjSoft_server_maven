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
public class NetworkRequest implements Serializable {

    public NetworkRequest(String requestType, Object payload) {
        this.requestType = requestType;
        this.payload = payload;
    }

    public NetworkRequest() {}
    
    public String requestType;
    public Object payload;
}
