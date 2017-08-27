package com.example.andre.particles.objects;

import android.opengl.GLES20;

import com.example.andre.particles.data.VertexArray;
import com.example.andre.particles.programs.SkyBoxShaderProgram;

import java.nio.ByteBuffer;

/**
 * Created by Andrei Nikitin
 */
public class SkyBox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public SkyBox(){
        vertexArray = new VertexArray(new float[]{
                -1,  1,  1,     // top left near
                 1,  1,  1,     // top right near
                -1, -1,  1,     // bottom left near
                 1, -1,  1,     // bottom right near
                -1,  1, -1,     // top left far
                 1,  1, -1,     // top right far
                -1, -1, -1,     // bottom left far
                 1, -1, -1,     // bottom right far
        });

        indexArray = ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[]{
                        //front
                        1, 3, 0,
                        0, 3, 2,

                        //back
                        4, 6, 5,
                        5, 6, 7,

                        //left
                        0, 2, 4,
                        4, 2, 6,

                        //right
                        5, 7, 1,
                        1, 7, 3,

                        //top
                        5, 1, 4,
                        4, 1, 0,

                        //bottom
                        6, 2, 7,
                        7, 2, 3,
                });

        indexArray.position(0);
    }

    public void bindData(SkyBoxShaderProgram program){
        vertexArray.setVertexAttribPointer(
                0,
                program.getPositionAttribLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw(){
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
    }
}
