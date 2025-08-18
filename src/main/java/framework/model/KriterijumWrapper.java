/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package framework.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Djurkovic
 */
public class KriterijumWrapper implements Serializable {

    //full - u potpunosti popuni objekat
    //shallow - popuni samo spoljne kljuceve
    //none - popuni iskljucivo atribute objekta
    public enum DepthLevel {
        FULL, SHALLOW, NONE
    };

    public List<KriterijumDescriptor> kds;
    public DepthLevel depthLevel;
    public List<FieldDescriptor> blacklist;

    public List<String> blacklistClass;

    public KriterijumWrapper(List<KriterijumDescriptor> kds, DepthLevel depthLevel) {
        this.kds = kds;
        this.depthLevel = depthLevel;
        this.blacklist = new ArrayList<>();
    }

    public KriterijumWrapper(List<KriterijumDescriptor> kds, DepthLevel depthLevel, List<FieldDescriptor> blacklist) {
        this.kds = kds;
        this.depthLevel = depthLevel;
        this.blacklist = blacklist;

    }

    public KriterijumWrapper(List<KriterijumDescriptor> kds, DepthLevel depthLevel, List<FieldDescriptor> blacklist, List<String> blacklistClass) {
        this.blacklistClass = blacklistClass;
        this.blacklist = blacklist;
        this.depthLevel = depthLevel;
        this.kds = kds;

    }

    public KriterijumWrapper() {
        this.blacklist = new ArrayList<>();
    }

    public KriterijumWrapper(DepthLevel depthLevel) {
        this.depthLevel = depthLevel;
    }

}
