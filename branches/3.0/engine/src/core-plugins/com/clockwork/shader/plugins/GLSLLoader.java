
package com.clockwork.shader.plugins;

import com.clockwork.asset.AssetInfo;
import com.clockwork.asset.AssetKey;
import com.clockwork.asset.AssetLoadException;
import com.clockwork.asset.AssetLoader;
import com.clockwork.asset.AssetManager;
import com.clockwork.asset.cache.AssetCache;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * GLSL File parser that supports #import pre-processor statement
 */
public class GLSLLoader implements AssetLoader {

    private AssetManager assetManager;
    private Map<String, ShaderDependencyNode> dependCache = new HashMap<String, ShaderDependencyNode>();

    /**
     * Used to load {@link ShaderDependencyNode}s.
     * Asset caching is disabled.
     */
    private class ShaderDependencyKey extends AssetKey<Reader> {

        public ShaderDependencyKey(String name) {
            super(name);
        }

        @Override
        public Class<? extends AssetCache> getCacheType() {
            // Disallow caching here
            return null;
        }
    }

    /**
     * Creates a {@link ShaderDependencyNode} from a stream representing shader code.
     * 
     * @param in The input stream containing shader code
     * @param nodeName
     * @return
     * @throws IOException 
     */
    private ShaderDependencyNode loadNode(Reader reader, String nodeName) {
        ShaderDependencyNode node = new ShaderDependencyNode(nodeName);

        StringBuilder sb = new StringBuilder();
        BufferedReader bufReader = new BufferedReader(reader);
        try {
            String ln;
            while ((ln = bufReader.readLine()) != null) {                
                if (ln.trim().startsWith("#import ")) {
                    ln = ln.trim().substring(8).trim();
                    if (ln.startsWith("\"") && ln.endsWith("\"") && ln.length() > 3) {
                        // import user code
                        // remove quotes to get filename
                        ln = ln.substring(1, ln.length() - 1);
                        if (ln.equals(nodeName)) {
                            throw new IOException("Node depends on itself.");
                        }

                        // check cache first
                        ShaderDependencyNode dependNode = dependCache.get(ln);

                        if (dependNode == null) {
                            Reader dependNodeReader = assetManager.loadAsset(new ShaderDependencyKey(ln));
                            dependNode = loadNode(dependNodeReader, ln);
                        }

                        node.addDependency(sb.length(), dependNode);
                    }
                } else {
                    sb.append(ln).append('\n');
                }
            }
        } catch (IOException ex) {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException ex1) {
                }
            }
            throw new AssetLoadException("Failed to load shader node: " + nodeName, ex);
        }

        node.setSource(sb.toString());
        dependCache.put(nodeName, node);
        return node;
    }

    private ShaderDependencyNode nextIndependentNode() throws IOException {
        Collection<ShaderDependencyNode> allNodes = dependCache.values();
        
        if (allNodes == null || allNodes.isEmpty()) {
            return null;
        }
        
        for (ShaderDependencyNode node : allNodes) {
            if (node.getDependOnMe().isEmpty()) {
                return node;
            }
        }

        // Circular dependency found..
        for (ShaderDependencyNode node : allNodes){
            System.out.println(node.getName());
        }
        
        throw new IOException("Circular dependency.");
    }
    
    private String resolveDependencies(ShaderDependencyNode node, Set<ShaderDependencyNode> alreadyInjectedSet) {
        if (alreadyInjectedSet.contains(node)) {
            return "// " + node.getName() + " was already injected at the top.\n";
        } else {
            alreadyInjectedSet.add(node);
        }
        if (node.getDependencies().isEmpty()) {
            return node.getSource();
        } else {
            StringBuilder sb = new StringBuilder(node.getSource());
            List<String> resolvedShaderNodes = new ArrayList<String>();
            for (ShaderDependencyNode dependencyNode : node.getDependencies()) {
                resolvedShaderNodes.add( resolveDependencies(dependencyNode, alreadyInjectedSet) );
            }
            List<Integer> injectIndices = node.getDependencyInjectIndices();
            for (int i = resolvedShaderNodes.size() - 1; i >= 0; i--) {
                // Must insert them backwards ..
                sb.insert(injectIndices.get(i), resolvedShaderNodes.get(i));
            }
            return sb.toString();
        }
    }

    public Object load(AssetInfo info) throws IOException {
        // The input stream provided is for the vertex shader, 
        // to retrieve the fragment shader, use the content manager
        this.assetManager = info.getManager();
        Reader reader = new InputStreamReader(info.openStream());
        if (info.getKey().getExtension().equals("glsllib")) {
            // NOTE: Loopback, GLSLLIB is loaded by this loader
            // and needs data as InputStream
            return reader;
        } else {
            ShaderDependencyNode rootNode = loadNode(reader, "[main]");
            String code = resolveDependencies(rootNode, new HashSet<ShaderDependencyNode>());
            dependCache.clear();
            return code;
        }
    }
}
