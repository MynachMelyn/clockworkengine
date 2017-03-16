
package com.clockwork.renderer;

import com.clockwork.material.FixedFuncBinding;

/**
 * Renderer sub-interface that is used for non-shader based renderers.
 * 
 * The GL1Renderer provides a single call, 
 * #setFixedFuncBinding(com.clockwork.material.FixedFuncBinding, java.lang.Object) }
 * which allows to set fixed functionality state.
 * 
 */
public interface GL1Renderer extends Renderer {
    
    /**
     * Set the fixed functionality state.
     * 
     * See FixedFuncBinding} for various values that
     * can be set.
     * 
     * @param ffBinding The binding to set
     * @param val The value
     */
    public void setFixedFuncBinding(FixedFuncBinding ffBinding, Object val);
}
