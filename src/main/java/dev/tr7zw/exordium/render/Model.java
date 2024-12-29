package dev.tr7zw.exordium.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.CompiledShaderProgram;

//#if MC >= 12100
import com.mojang.blaze3d.vertex.MeshData;
//#if MC >= 12102
import com.mojang.blaze3d.buffers.BufferUsage;
//#endif
//#else
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//#endif

public class Model {
    private VertexBuffer toDraw;

    public Model(Vector3f[] modelData, Vector2f[] uvData) {
        // 4 bytes per float, 5 floats per entry
        //#if MC >= 12100
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX); //new BufferBuilder(modelData.length * 4 * 5);
        //#else
        //$$ BufferBuilder bufferbuilder = new BufferBuilder(modelData.length * 4 * 5);
        //$$ bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        //#endif
        for (int i = 0; i < modelData.length; i++) {
            Vector3f pos = modelData[i];
            Vector2f uv = uvData[i];
            //#if MC >= 12100
            bufferbuilder.addVertex(pos.x(), pos.y(), pos.z()).setUv(uv.x(), uv.y());
            //#else
            //$$ bufferbuilder.vertex(pos.x(), pos.y(), pos.z()).uv(uv.x(), uv.y()).endVertex();
            //#endif
        }
        //#if MC >= 12102
        toDraw = new VertexBuffer(BufferUsage.STATIC_WRITE);
        //#else
        //$$toDraw = new VertexBuffer(VertexBuffer.Usage.STATIC);
        //#endif
        //#if MC >= 12100
        upload(bufferbuilder.build());
        //#else
        //$$ upload(bufferbuilder.end());
        //#endif
    }

    public void drawWithShader(Matrix4f matrix4f, Matrix4f matrix4f2, CompiledShaderProgram shaderInstance) {
        toDraw.bind();
        toDraw.drawWithShader(matrix4f, matrix4f2, shaderInstance);
    }

    public void draw(Matrix4f matrix4f) {
        drawWithShader(matrix4f, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    //#if MC >= 12100
    private void upload(MeshData renderedBuffer) {
    //#else
    //$$ private void upload(BufferBuilder.RenderedBuffer renderedBuffer) {
    //#endif
        RenderSystem.assertOnRenderThread();
        toDraw.bind();
        toDraw.upload(renderedBuffer);
    }

    public void close() {
        toDraw.close();
    }

    public record Vector2f(float x, float y) {
    }

}
