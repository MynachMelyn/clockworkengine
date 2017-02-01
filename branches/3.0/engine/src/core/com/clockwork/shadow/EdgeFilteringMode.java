
package com.clockwork.shadow;

/**
 * <code>ShadowEdgeFiltering</code> specifies how shadows are filtered
 */
public enum EdgeFilteringMode {

    /**
     * Shadows are not filtered. Nearest sample is used, causing in blocky
     * shadows.
     */
    Nearest(0),
    /**
     * Bilinear filtering is used. Has the potential of being hardware
     * accelerated on some GPUs
     */
    Bilinear(1),
    /**
     * Dither-based sampling is used, very cheap but can look bad at low
     * resolutions.
     */
    Dither(2),
    /**
     * 4x4 percentage-closer filtering is used. Shadows will be smoother at the
     * cost of performance
     */
    PCF4(3),
    /**
     * 12 samples percentage-closer filtering with a POISON disc distribution 
     * is used. 
     * http://devmag.org.za/2009/05/03/poisson-disk-sampling/
     * The principle is to eliminate the regular blurring pattern that can be 
     * seen with pcf4x4 by randomizing the samble position with a poisson disc.
     * Shadows will look smoother than 4x4 PCF but with slightly better or 
     * similar performance.
     */
    PCFPOISSON(4),
    /**
     * 8x8 percentage-closer filtering is used. Shadows will be smoother at the
     * cost of performance
     */
    PCF8(5);
    
    int materialParamValue;

    private EdgeFilteringMode(int val) {
        materialParamValue = val;
    }

    public int getMaterialParamValue() {
        return materialParamValue;
    }
    
}
