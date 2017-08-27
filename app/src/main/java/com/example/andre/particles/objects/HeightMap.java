package com.example.andre.particles.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;

import com.example.andre.particles.Constants;
import com.example.andre.particles.data.IndexBuffer;
import com.example.andre.particles.data.VertexBuffer;
import com.example.andre.particles.programs.HeightMapShaderProgram;
import com.example.andre.particles.util.Geometry;
import com.example.andre.particles.util.Geometry.Point;
import com.example.andre.particles.util.Geometry.Vector;

/**
 * Created by Andrei Nikitin
 */
public class HeightMap {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COMPONENT_COUNT = 2;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT +
            NORMAL_COMPONENT_COUNT +
            TEXTURE_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final int width;
    private final int height;
    private final int numElements;
    private final int textureScale;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public HeightMap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        textureScale = 60;

        if(width * height > 65536){
            throw new RuntimeException("HeightMap is too large for the index buffer");
        }

        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;

        for(int row = 0; row < height - 1; row++){
            for (int col = 0; col < width - 1; col++){
                short topLeftIndexNum = (short) (row * width + col);
                short topRightIndexNum = (short) (row * width + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * width + col);
                short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);

                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }

        return indexData;
    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float heightMapVertices[] =
                new float[width * height * TOTAL_COMPONENT_COUNT];
        int offset = 0;

        for(int row = 0; row < height; row++){
            for (int col = 0; col < width; col++){
                final Point point = getPoint(pixels, row, col);
                heightMapVertices[offset++] = point.x;
                heightMapVertices[offset++] = point.y;
                heightMapVertices[offset++] = point.z;

                final Point top = getPoint(pixels, row - 1, col);
                final Point left = getPoint(pixels, row, col - 1);
                final Point right = getPoint(pixels, row, col + 1);
                final Point bottom = getPoint(pixels, row + 1, col);

                final Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Vector topToBottom = Geometry.vectorBetween(top, bottom);
                final Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightMapVertices[offset++] = normal.x;
                heightMapVertices[offset++] = normal.y;
                heightMapVertices[offset++] = normal.z;

                heightMapVertices[offset++] = (float) col / (float) (width) * textureScale;
                heightMapVertices[offset++] = (float) row / (float) (height) * textureScale;
            }
        }

        return heightMapVertices;
    }

    private Point getPoint(int[] pixels, int row, int col) {
        final float xPosition = (float) col / (float)(width - 1) - 0.5f;
        final float zPosition = (float) row / (float)(height - 1) - 0.5f;

        row = clamp(row, 0, width - 1);
        col = clamp(col, 0, height - 1);

        final float yPosition = (float) Color.red(pixels[row * height + col]) / 255f;

        return new Point(xPosition, yPosition, zPosition);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max ,val));
    }

    public void bindData(HeightMapShaderProgram program){
        vertexBuffer.setVertexAttribPointer(
                0,
                program.getPositionAttribLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );

        vertexBuffer.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT,
                program.getNormalAttribLocation(),
                NORMAL_COMPONENT_COUNT,
                STRIDE
        );

        vertexBuffer.setVertexAttribPointer(
                (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT,
                program.getTextureCoordinatesAttribLocation(),
                TEXTURE_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
