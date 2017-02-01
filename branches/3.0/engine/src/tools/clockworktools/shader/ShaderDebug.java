
package clockworktools.shader;

/**
 * Static tool box class for convenient methods to help debug shaders
 */
public class ShaderDebug {

    /**
     * Append the line numbers to the source code of a shader to output it
     * @param defines the defines
     * @param source the source
     * @return the formated source code
     */
    public static String formatShaderSource(String defines, String source, String version) {
        String[] versionLines = version.split("\n");
        String[] definesLines = defines.split("\n");
        String[] sourceLines = source.split("\n");
        int nblines = 0;
        StringBuilder out = new StringBuilder();
        if (!version.equals("")) {
            for (String string : versionLines) {
                nblines++;
                out.append(nblines).append("\t").append(string).append("\n");
            }
        }
        if (!defines.equals("")) {
            for (String string : definesLines) {
                nblines++;
                out.append(nblines).append("\t").append(string).append("\n");
            }
        }
        for (String string : sourceLines) {
            nblines++;
            out.append(nblines).append("\t").append(string).append("\n");
        }
        return out.toString();
    }
}
