
package com.clockwork.shader;

import com.clockwork.export.*;
import com.clockwork.material.MatParam;
import com.clockwork.material.TechniqueDef;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class DefineList implements Savable, Cloneable {

    private static final String ONE = "1";
    
    private TreeMap<String, String> defines = new TreeMap<String, String>();
    private String compiled = null;
    private int cachedHashCode = 0;

    public void write(CWExporter ex) throws IOException{
        OutputCapsule oc = ex.getCapsule(this);

        String[] keys = new String[defines.size()];
        String[] vals = new String[defines.size()];

        int i = 0;
        for (Map.Entry<String, String> define : defines.entrySet()){
            keys[i] = define.getKey();
            vals[i] = define.getValue();
            i++;
        }

        oc.write(keys, "keys", null);
        oc.write(vals, "vals", null);
    }

    public void read(CWImporter im) throws IOException{
        InputCapsule ic = im.getCapsule(this);

        String[] keys = ic.readStringArray("keys", null);
        String[] vals = ic.readStringArray("vals", null);
        for (int i = 0; i < keys.length; i++){
            defines.put(keys[i], vals[i]);
        }
    }

    public void clear() {
        defines.clear();
        compiled = "";
        cachedHashCode = 0;
    }

    public String get(String key){
        return defines.get(key);
    }
    
    @Override
    public DefineList clone() {
        try {
            DefineList clone = (DefineList) super.clone();
            clone.cachedHashCode = 0;
            clone.compiled = null;
            clone.defines = (TreeMap<String, String>) defines.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

    public boolean set(String key, VarType type, Object val){    
        if (val == null){
            defines.remove(key);
            compiled = null;
            cachedHashCode = 0;
            return true;
        }

        switch (type){
            case Boolean:
                if (((Boolean) val).booleanValue()) {
                    // same literal, != will work
                    if (defines.put(key, ONE) != ONE) {
                        compiled = null;
                        cachedHashCode = 0;
                        return true;
                    }
                } else if (defines.containsKey(key)) {
                    defines.remove(key);
                    compiled = null;
                    cachedHashCode = 0;
                    return true;
                }
                
                break;
            case Float:
            case Int:
                String newValue = val.toString();
                String original = defines.put(key, newValue);
                if (!val.equals(original)) {
                    compiled = null;
                    cachedHashCode = 0;
                    return true;            
                }
                break;
            default:
                // same literal, != will work
                if (defines.put(key, ONE) != ONE) {  
                    compiled = null;
                    cachedHashCode = 0;
                    return true;            
                }
                break;
        }
        
        return false;
    }

    public boolean remove(String key){   
        if (defines.remove(key) != null) {
            compiled = null;
            cachedHashCode = 0;
            return true;
        }
        return false;
    }

    public void addFrom(DefineList other){    
        if (other == null) {
            return;
        }
        compiled = null;
        cachedHashCode = 0;
        defines.putAll(other.defines);
    }

    public String getCompiled(){
        if (compiled == null){
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : defines.entrySet()){
                sb.append("#define ").append(entry.getKey()).append(" ");
                sb.append(entry.getValue()).append('\n');
            }
            compiled = sb.toString();
        }
        return compiled;
    }

    @Override
    public boolean equals(Object obj) {
        final DefineList other = (DefineList) obj;
        return defines.equals(other.defines);
    }
    
    public boolean equalsParams(Collection<MatParam> params, TechniqueDef def) {

        int size = 0;

        for (MatParam param : params) {
            String key = def.getShaderParamDefine(param.getName());
            if (key != null) {
                Object val = param.getValue();
                if (val != null) {

                    switch (param.getVarType()) {
                    case Boolean: {
                        String current = defines.get(key);
                        if (((Boolean) val).booleanValue()) {
                            if (current == null || current != ONE) {
                                return false;
                            }
                            size++;
                        } else {
                            if (current != null) {
                                return false;
                            }
                        }
                    }
                        break;
                    case Float:
                    case Int: {
                        String newValue = val.toString();
                        String current = defines.get(key);
                        if (!newValue.equals(current)) {
                            return false;
                        }
                        size++;
                    }
                        break;
                    default: {
                        if (!defines.containsKey(key)) {
                            return false;
                        }
                        size++;
                    }
                        break;
                    }

                }

            }
        }

        if (size != defines.size()) {
            return false;
        }

        return true;
    }
    
    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            cachedHashCode = defines.hashCode();
        }
        return cachedHashCode;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry : defines.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (i != defines.size() - 1) {
                sb.append(", ");
            }
            i++;
        }
        return sb.toString();
    }

}
