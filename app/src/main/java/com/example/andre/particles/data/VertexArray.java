package com.example.andre.particles.data;


import com.example.andre.particles.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Andrei Nikitin
 */
public class VertexArray {
    private final FloatBuffer vertexData;

    public VertexArray (float vertexData[]){
        this.vertexData = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttribPointer(int offset, int location, int componentCount, int stride){
        vertexData.position(offset);
        glVertexAttribPointer(location, componentCount, GL_FLOAT, false, stride, vertexData);
        glEnableVertexAttribArray(location);
        vertexData.position(0);
    }

    public void updateBuffer(float[] particles, int particleOffset, int totalComponentCount) {
        vertexData.position(particleOffset);
        vertexData.put(particles, particleOffset, totalComponentCount);
        vertexData.position(0);
    }
}
