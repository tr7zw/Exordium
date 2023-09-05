package dev.tr7zw.exordium.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.ShaderInstance;

public class Model {
    VertexBuffer toDraw;
    int vertexCount;

    public Model(Vector3f[] modelData, Vector2f[] uvData) {

        BufferBuilder bufferbuilder = new BufferBuilder(modelData.length);

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < modelData.length; i++) {
            Vector3f pos = modelData[i];
            Vector2f uv = uvData[i];
            bufferbuilder.vertex(pos.x(), pos.y(), pos.z()).uv(uv.x(), uv.y()).endVertex();
        }
        toDraw = new VertexBuffer(VertexBuffer.Usage.STATIC);
        upload(bufferbuilder.end());
    }

    public void drawWithShader(Matrix4f matrix4f, Matrix4f matrix4f2, ShaderInstance shaderInstance) {
        toDraw.bind();
        toDraw.drawWithShader(matrix4f, matrix4f2, shaderInstance);
    }

    public void draw(Matrix4f matrix4f) {
        drawWithShader(matrix4f, RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }

    private void upload(BufferBuilder.RenderedBuffer renderedBuffer) {
        RenderSystem.assertOnRenderThread();
        if (renderedBuffer.isEmpty()) {
            renderedBuffer.release();
        } else {
            toDraw.bind();
            toDraw.upload(renderedBuffer);
        }
    }

    public void close() {
        toDraw.close();
    }

    public record Vector2f(float x, float y) {
    }

}
