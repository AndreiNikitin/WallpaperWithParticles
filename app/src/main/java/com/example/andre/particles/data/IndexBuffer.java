package com.example.andre.particles.data;

import com.example.andre.particles.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Andrei Nikitin
 */
public class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] indices){
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if(buffers[0] == 0){
            throw new RuntimeException("Could not create a new vertex index buffer object.");
        }
        bufferId = buffers[0];

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);

        ShortBuffer indicesBuffer = ByteBuffer
                .allocateDirect(indices.length * Constants.BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indices);
        indicesBuffer.position(0);

        glBufferData(
                GL_ELEMENT_ARRAY_BUFFER,
                indicesBuffer.capacity() * Constants.BYTES_PER_SHORT,
                indicesBuffer,
                GL_STATIC_DRAW
        );

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
