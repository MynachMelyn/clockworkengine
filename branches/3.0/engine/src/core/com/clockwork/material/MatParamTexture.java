
package com.clockwork.material;

import com.clockwork.export.InputCapsule;
import com.clockwork.export.JmeExporter;
import com.clockwork.export.JmeImporter;
import com.clockwork.export.OutputCapsule;
import com.clockwork.renderer.Renderer;
import com.clockwork.shader.VarType;
import com.clockwork.texture.Texture;
import java.io.IOException;

public class MatParamTexture extends MatParam {

    private Texture texture;
    private int unit;

    public MatParamTexture(VarType type, String name, Texture texture, int unit) {
        super(type, name, texture, null);
        this.texture = texture;
        this.unit = unit;
    }

    public MatParamTexture() {
    }

    public Texture getTextureValue() {
        return texture;
    }

    public void setTextureValue(Texture value) {
        this.value = value;
        this.texture = value;
    }
    
    @Override
    public void setValue(Object value) {
        if (!(value instanceof Texture)) {
            throw new IllegalArgumentException("value must be a texture object");
        }
        this.value = value;
        this.texture = (Texture) value;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getUnit() {
        return unit;
    }

    @Override
    public void apply(Renderer r, Technique technique) {
        TechniqueDef techDef = technique.getDef();
        r.setTexture(getUnit(), getTextureValue());
        if (techDef.isUsingShaders()) {
            technique.updateUniformParam(getPrefixedName(), getVarType(), getUnit());
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(unit, "texture_unit", -1);
        
        // For backwards compat
        oc.write(texture, "texture", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        unit = ic.readInt("texture_unit", -1);
        texture = (Texture) value;
        //texture = (Texture) ic.readSavable("texture", null);
    }
}