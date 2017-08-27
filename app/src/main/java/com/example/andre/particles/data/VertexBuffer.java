package com.example.andre.particles.data;

import com.example.andre.particles.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Andrei Nikitin
 */
public class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData){
        final int[] buffers = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if(buffers[0] == 0){
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }
        bufferId = buffers[0];

        glBindBuffer(GL_ARRAY_BUFFER, bufferId);

        FloatBuffer vertexArray = ByteBuffer
                .allocateDirect(vertexData.length * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexArray.position(0);

        glBufferData(
                GL_ARRAY_BUFFER,
                vertexArray.capacity() * Constants.BYTES_PER_FLOAT,
                vertexArray,
                GL_STATIC_DRAW
        );

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttribPointer(int offset, int attribLocation, int count, int stride){
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attribLocation, count, GL_FLOAT, false, stride, offset);
        glEnableVertexAttribArray(attribLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
