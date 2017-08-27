package com.example.andre.particles.programs;

import android.content.Context;

import com.example.andre.particles.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Andrei Nikitin
 */
public class SkyBoxShaderProgram extends ShaderProgram{
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;

    public SkyBoxShaderProgram (Context context){
        super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader);

        uMatrixLocation = glGetUniformLocation(programId, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(programId, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(programId, A_POSITION);
    }

    public void setUniforms(float[] matrix, int texture){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttribLocation() {
        return aPositionLocation;
    }
}
