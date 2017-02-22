
package com.clockwork.niftygui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.clockwork.asset.TextureKey;
import com.clockwork.material.Material;
import com.clockwork.material.RenderState;
import com.clockwork.math.Matrix4f;
import com.clockwork.renderer.RenderManager;
import com.clockwork.renderer.Renderer;
import com.clockwork.scene.Geometry;
import com.clockwork.scene.Mesh;
import com.clockwork.scene.VertexBuffer;
import com.clockwork.scene.VertexBuffer.Type;
import com.clockwork.scene.VertexBuffer.Usage;
import com.clockwork.texture.Image.Format;
import com.clockwork.texture.Texture.MagFilter;
import com.clockwork.texture.Texture.MinFilter;
import com.clockwork.texture.Texture2D;
import com.clockwork.util.BufferUtils;

import de.lessvoid.nifty.batch.spi.BatchRenderBackend;
import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.ObjectPool;
import de.lessvoid.nifty.tools.ObjectPool.Factory;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;

/**
 * Nifty GUI BatchRenderBackend Implementation for jMonkeyEngine.
 * 
 */
public class JmeBatchRenderBackend implements BatchRenderBackend {
  private static Logger log = Logger.getLogger(JmeBatchRenderBackend.class.getName());

  private final ObjectPool<Batch> batchPool;
  private final List<Batch> batches = new ArrayList<Batch>();

  // a modify texture call needs a jme Renderer to execute. if we're called to modify a texture but don't
  // have a Renderer yet - since it was not initialized on the jme side - we'll cache the modify texture calls
  // in here and execute them later (at the next beginFrame() call).
  private final List<ModifyTexture> modifyTextureCalls = new ArrayList<ModifyTexture>();

  private RenderManager renderManager;
  private NiftyJmeDisplay display;
  private Texture2D textureAtlas;
  private Batch currentBatch;
  private Matrix4f tempMat = new Matrix4f();
  private ByteBuffer initialData;

  // this is only used for debugging purpose and will make the removed textures filled with a color
  private boolean fillRemovedTexture =
      Boolean.getBoolean(System.getProperty(JmeBatchRenderBackend.class.getName() + ".fillRemovedTexture", "false"));

  public JmeBatchRenderBackend(final NiftyJmeDisplay display) {
    this.display = display;
    this.batchPool = new ObjectPool<Batch>(2, new Factory<Batch>() {
      @Override
      public Batch createNew() {
        return new Batch();
      }
    });
  }

  public void setRenderManager(final RenderManager rm) {
    this.renderManager = rm;
  }

  @Override
  public void setResourceLoader(final NiftyResourceLoader resourceLoader) {
  }

  @Override
  public int getWidth() {
    return display.getWidth();
  }

  @Override
  public int getHeight() {
    return display.getHeight();
  }

  @Override
  public void beginFrame() {
    log.fine("beginFrame()");

    for (int i=0; i<batches.size(); i++) {
      batchPool.free(batches.get(i));
    }
    batches.clear();

    // in case we have pending modifyTexture calls we'll need to execute them now
    if (!modifyTextureCalls.isEmpty()) {
      Renderer renderer = display.getRenderer();
      for (int i=0; i<modifyTextureCalls.size(); i++) {
        modifyTextureCalls.get(i).execute(renderer);
      }
      modifyTextureCalls.clear();
    }
  }

  @Override
  public void endFrame() {
    log.fine("endFrame");
  }

  @Override
  public void clear() {
  }

  // TODO: Cursor support

  @Override
  public MouseCursor createMouseCursor(final String filename, final int hotspotX, final int hotspotY) throws IOException {
    return new MouseCursor() {
      public void dispose() {
      }
  };
  }

  @Override
  public void enableMouseCursor(final MouseCursor mouseCursor) {
  }

  @Override
  public void disableMouseCursor() {
  }

  @Override
  public void createAtlasTexture(final int width, final int height) {
    try {
      createAtlasTextureInternal(width, height);

      // we just initialize a second buffer here that will replace the texture atlas image
      initialData = BufferUtils.createByteBuffer(width*height*4);
      for (int i=0; i<width*height; i++) {
        initialData.put((byte) 0x00);
        initialData.put((byte) 0xff);
        initialData.put((byte) 0x00);
        initialData.put((byte) 0xff);
      }
    } catch (Exception e) {
      log.log(Level.WARNING, e.getMessage(), e);
    }
  }

  @Override
  public void clearAtlasTexture(final int width, final int height) {
    initialData.rewind();
    textureAtlas.getImage().setData(initialData);
  }

  @Override
  public Image loadImage(final String filename) {
    TextureKey key = new TextureKey(filename, false);
    key.setAnisotropy(0);
    key.setAsCube(false);
    key.setGenerateMips(false);

    Texture2D texture = (Texture2D) display.getAssetManager().loadTexture(key);
    return new ImageImpl(texture.getImage());
  }

  @Override
  public void addImageToTexture(final Image image, final int x, final int y) {
    ImageImpl imageImpl = (ImageImpl) image;
    imageImpl.modifyTexture(this, textureAtlas, x, y);
  }

  @Override
  public void beginBatch(final BlendMode blendMode) {
    batches.add(batchPool.allocate());
    currentBatch = batches.get(batches.size() - 1);
    currentBatch.begin(blendMode);
  }

  @Override
  public void addQuad(
      final float x,
      final float y,
      final float width,
      final float height,
      final Color color1,
      final Color color2,
      final Color color3,
      final Color color4,
      final float textureX,
      final float textureY,
      final float textureWidth,
      final float textureHeight) {
    if (!currentBatch.canAddQuad()) {
      beginBatch(currentBatch.getBlendMode());
    }
    currentBatch.addQuadInternal(x, y, width, height, color1, color2, color3, color4, textureX, textureY, textureWidth, textureHeight);
  }

  @Override
  public int render() {
    for (int i=0; i<batches.size(); i++) {
      Batch batch = batches.get(i);
      batch.render();
    }
    return batches.size();
  }

  @Override
  public void removeFromTexture(final Image image, final int x, final int y, final int w, final int h) {
    // Since we clear the whole texture when we switch screens it's not really necessary to remove data from the
    // texture atlas when individual textures are removed. If necessary this can be enabled with a system property.
    if (!fillRemovedTexture) {
      return;
    }

    ByteBuffer initialData = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
    for (int i=0; i<image.getWidth()*image.getHeight(); i++) {
      initialData.put((byte) 0xff);
      initialData.put((byte) 0x00);
      initialData.put((byte) 0x00);
      initialData.put((byte) 0xff);
    }
    initialData.rewind();
    modifyTexture(
        textureAtlas,
        new com.clockwork.texture.Image(Format.RGBA8, image.getWidth(), image.getHeight(), initialData),
        x,
        y);
  }

  // internal implementations

  private void createAtlasTextureInternal(final int width, final int height) throws Exception {
    ByteBuffer initialData = BufferUtils.createByteBuffer(width*height*4);
    for (int i=0; i<width*height*4; i++) {
      initialData.put((byte) 0x80);
    }
    initialData.rewind();

    textureAtlas = new Texture2D(new com.clockwork.texture.Image(Format.RGBA8, width, height, initialData));
    textureAtlas.setMinFilter(MinFilter.NearestNoMipMaps);
    textureAtlas.setMagFilter(MagFilter.Nearest);
  }

  private void modifyTexture(
      final Texture2D textureAtlas,
      final com.clockwork.texture.Image image,
      final int x,
      final int y) {
    Renderer renderer = display.getRenderer();
    if (renderer == null) {
      // we have no renderer (yet) so we'll need to cache this call to the next beginFrame() call
      modifyTextureCalls.add(new ModifyTexture(textureAtlas, image, x, y));
      return;
    }

    // all is well, we can execute the modify right away
    renderer.modifyTexture(textureAtlas, image, x, y);
  }

  /**
   * Simple BatchRenderBackend.Image implementation that will transport the dimensions of an image as well as the
   * actual bytes from the loadImage() to the addImageToTexture() method.
   *
   * 
   */
  private static class ImageImpl implements BatchRenderBackend.Image {
    private final com.clockwork.texture.Image image;

    public ImageImpl(final com.clockwork.texture.Image image) {
      this.image = image;
    }

    public void modifyTexture(
        final JmeBatchRenderBackend backend,
        final Texture2D textureAtlas,
        final int x,
        final int y) {
      backend.modifyTexture(textureAtlas, image, x, y);
    }

    @Override
    public int getWidth() {
      return image.getWidth();
    }

    @Override
    public int getHeight() {
      return image.getHeight();
    }
  }

  /**
   * Used to delay ModifyTexture calls in case we don't have a JME3 Renderer yet.
   * 
   */
  private static class ModifyTexture {
    private Texture2D atlas;
    private com.clockwork.texture.Image image;
    private int x;
    private int y;

    private ModifyTexture(final Texture2D atlas, final com.clockwork.texture.Image image, final int x, final int y) {
      this.atlas = atlas;
      this.image = image;
      this.x = x;
      this.y = y;
    }

    public void execute(final Renderer renderer) {
      renderer.modifyTexture(atlas, image, x, y);
    }
  }

  /**
   * This class helps us to manage the batch data. We'll keep a bunch of instances of this class around that will be
   * reused when needed. Each Batch instance provides room for a certain amount of vertices and we'll use a new Batch
   * when we exceed this amount of data.
   *
   * 
   */
  private class Batch {
    // 4 vertices per quad and 8 vertex attributes for each vertex:
    // - 2 x pos
    // - 2 x texture
    // - 4 x color
    //
    // stored into 3 different buffers: position, texture coords, vertex color
    // and an additional buffer for indexes
    //
    // there is a fixed amount of primitives per batch. if we run out of vertices we'll start a new batch.
    private final static int BATCH_MAX_QUADS = 2000;
    private final static int BATCH_MAX_VERTICES = BATCH_MAX_QUADS * 4;

    // individual buffers for all the vertex attributes
    private final VertexBuffer vertexPos = new VertexBuffer(Type.Position);
    private final VertexBuffer vertexTexCoord = new VertexBuffer(Type.TexCoord);
    private final VertexBuffer vertexColor = new VertexBuffer(Type.Color);
    private final VertexBuffer indexBuffer = new VertexBuffer(Type.Index);

    private final Mesh mesh = new Mesh();
    private final Geometry meshGeometry = new Geometry("nifty-quad", mesh);
    private final RenderState renderState = new RenderState();

    private FloatBuffer vertexPosBuffer;
    private FloatBuffer vertexTexCoordBuffer;
    private FloatBuffer vertexColorBuffer;
    private ShortBuffer indexBufferBuffer;

    // number of quads already added to this batch.
    private int quadCount;
    private short globalVertexIndex;

    // current blend mode
    private BlendMode blendMode = BlendMode.BLEND;
    private Material material;

    public Batch() {
      // setup mesh
      vertexPos.setupData(Usage.Stream, 2, VertexBuffer.Format.Float, BufferUtils.createFloatBuffer(BATCH_MAX_VERTICES * 2));
      vertexPosBuffer = (FloatBuffer) vertexPos.getData();
      mesh.setBuffer(vertexPos);

      vertexTexCoord.setupData(Usage.Stream, 2, VertexBuffer.Format.Float, BufferUtils.createFloatBuffer(BATCH_MAX_VERTICES * 2));
      vertexTexCoordBuffer = (FloatBuffer) vertexTexCoord.getData();
      mesh.setBuffer(vertexTexCoord);

      vertexColor.setupData(Usage.Stream, 4, VertexBuffer.Format.Float, BufferUtils.createFloatBuffer(BATCH_MAX_VERTICES * 4));
      vertexColorBuffer = (FloatBuffer) vertexColor.getData();
      mesh.setBuffer(vertexColor);

      indexBuffer.setupData(Usage.Stream, 3, VertexBuffer.Format.UnsignedShort, BufferUtils.createShortBuffer(BATCH_MAX_QUADS * 2 * 3));
      indexBufferBuffer = (ShortBuffer) indexBuffer.getData();
      mesh.setBuffer(indexBuffer);

      material = new Material(display.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
      material.setBoolean("VertexColor", true);

      renderState.setDepthTest(false);
      renderState.setDepthWrite(false);
    }

    public void begin(final BlendMode blendMode) {
      this.blendMode = blendMode;
      quadCount = 0;
      globalVertexIndex = 0;
      vertexPosBuffer.clear();
      vertexTexCoordBuffer.clear();
      vertexColorBuffer.clear();
      indexBufferBuffer.clear();
    }

    public BlendMode getBlendMode() {
      return blendMode;
    }

    public void render() {
      renderState.setBlendMode(convertBlend(blendMode));

      vertexPosBuffer.flip();
      vertexPos.updateData(vertexPosBuffer);

      vertexTexCoordBuffer.flip();
      vertexTexCoord.updateData(vertexTexCoordBuffer);

      vertexColorBuffer.flip();
      vertexColor.updateData(vertexColorBuffer);

      indexBufferBuffer.flip();
      indexBuffer.updateData(indexBufferBuffer);

      tempMat.loadIdentity();
      renderManager.setWorldMatrix(tempMat);
      renderManager.setForcedRenderState(renderState);

      material.setTexture("ColorMap", textureAtlas);
      material.render(meshGeometry, renderManager);
      renderManager.setForcedRenderState(null);
    }

    private RenderState.BlendMode convertBlend(final BlendMode blendMode) {
      if (blendMode == null) {
          return RenderState.BlendMode.Off;
      } else if (blendMode == BlendMode.BLEND) {
          return RenderState.BlendMode.Alpha;
      } else if (blendMode == BlendMode.MULIPLY) {
          return RenderState.BlendMode.Modulate;
      } else {
          throw new UnsupportedOperationException();
      }
  }

    public boolean canAddQuad() {
      return (quadCount + 1) < BATCH_MAX_QUADS;
    }

    private void addQuadInternal(
        final float x,
        final float y,
        final float width,
        final float height,
        final Color color1,
        final Color color2,
        final Color color3,
        final Color color4,
        final float textureX,
        final float textureY,
        final float textureWidth,
        final float textureHeight) {
      indexBufferBuffer.put((short)(globalVertexIndex + 0));
      indexBufferBuffer.put((short)(globalVertexIndex + 3));
      indexBufferBuffer.put((short)(globalVertexIndex + 2));

      indexBufferBuffer.put((short)(globalVertexIndex + 0));
      indexBufferBuffer.put((short)(globalVertexIndex + 2));
      indexBufferBuffer.put((short)(globalVertexIndex + 1));

      addVertex(x,         y,          textureX,                textureY,                 color1);
      addVertex(x + width, y,          textureX + textureWidth, textureY,                 color2);
      addVertex(x + width, y + height, textureX + textureWidth, textureY + textureHeight, color4);
      addVertex(x,         y + height, textureX,                textureY + textureHeight, color3);

      quadCount++;
      globalVertexIndex += 4;
    }

    private void addVertex(final float x, final float y, final float tx, final float ty, final Color c) {
      vertexPosBuffer.put(x);
      vertexPosBuffer.put(getHeight() - y);
      vertexTexCoordBuffer.put(tx);
      vertexTexCoordBuffer.put(ty);
      vertexColorBuffer.put(c.getRed());
      vertexColorBuffer.put(c.getGreen());
      vertexColorBuffer.put(c.getBlue());
      vertexColorBuffer.put(c.getAlpha());
    }
  }
}
