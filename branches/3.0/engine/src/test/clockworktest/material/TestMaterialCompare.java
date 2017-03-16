
package clockworktest.material;

import com.clockwork.asset.AssetManager;
import com.clockwork.asset.TextureKey;
import com.clockwork.material.Material;
import com.clockwork.material.RenderState.BlendMode;
import com.clockwork.math.ColorRGBA;
import com.clockwork.system.CWSystem;
import com.clockwork.texture.Texture;

public class TestMaterialCompare {

    public static void main(String[] args) {
        AssetManager assetManager = CWSystem.newAssetManager(
                TestMaterialCompare.class.getResource("/com/clockwork/asset/Desktop.cfg"));
        
        // Cloned materials
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setName("mat1");
        mat1.setColor("Color", ColorRGBA.Blue);

        Material mat2 = mat1.clone();
        mat2.setName("mat2");
        testEquality(mat1, mat2, true);

        // Cloned material with different render states
        Material mat3 = mat1.clone();
        mat3.setName("mat3");
        mat3.getAdditionalRenderState().setBlendMode(BlendMode.ModulateX2);
        testEquality(mat1, mat3, false);

        // Two separately loaded materials
        Material mat4 = assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m");
        mat4.setName("mat4");
        Material mat5 = assetManager.loadMaterial("Models/Sign Post/Sign Post.j3m");
        mat5.setName("mat5");
        testEquality(mat4, mat5, true);
        
        // Comparing same textures
        TextureKey originalKey = (TextureKey) mat4.getTextureParam("DiffuseMap").getTextureValue().getKey();
        TextureKey tex1key = new TextureKey("Models/Sign Post/Sign Post.jpg", false);
        tex1key.setGenerateMips(true);
        
        // Texture keys from the original and the loaded texture
        // must be identical, otherwise the resultant textures not identical
        // and thus materials are not identical!
        if (!originalKey.equals(tex1key)){
            System.out.println("TEXTURE KEYS ARE NOT EQUAL");
        }
        
        Texture tex1 = assetManager.loadTexture(tex1key);
        mat4.setTexture("DiffuseMap", tex1);
        testEquality(mat4, mat5, true);
        
        // Change some stuff on the texture and compare, materials no longer equal
        tex1.setWrap(Texture.WrapMode.MirroredRepeat);
        testEquality(mat4, mat5, false);
        
        // Comparing different textures
        Texture tex2 = assetManager.loadTexture("Interface/Logo/Monkey.jpg");
        mat4.setTexture("DiffuseMap", tex2);
        testEquality(mat4, mat5, false);

        // Two materials created the same way
        Material mat6 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat6.setName("mat6");
        mat6.setColor("Color", ColorRGBA.Blue);
        testEquality(mat1, mat6, true);

        // Changing a material param
        mat6.setColor("Color", ColorRGBA.Green);
        testEquality(mat1, mat6, false);
    }

    private static void testEquality(Material mat1, Material mat2, boolean expected) {
        if (mat2.contentEquals(mat1)) {
            System.out.print(mat1.getName() + " == " + mat2.getName());
            if (expected) {
                System.out.println(" EQUAL OK");
            } else {
                System.out.println(" EQUAL FAIL!");
            }
        } else {
            System.out.print(mat1.getName() + " != " + mat2.getName());
            if (!expected) {
                System.out.println(" EQUAL OK");
            } else {
                System.out.println(" EQUAL FAIL!");
            }
        }
        if (mat2.hashCode() == mat1.hashCode()){
            System.out.print(mat1.getName() + " == " + mat2.getName());
            if (expected) {
                System.out.println(" HASH OK");
            } else {
                System.out.println(" HASH FAIL!");
            }
        } else {
            System.out.print(mat1.getName() + " != " + mat2.getName());
            if (!expected) {
                System.out.println(" HASH OK");
            } else {
                System.out.println(" HASH FAIL!");
            }
        }
    }
}
