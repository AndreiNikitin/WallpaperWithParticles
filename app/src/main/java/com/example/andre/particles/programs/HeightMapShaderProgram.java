package com.example.andre.particles.programs;

import android.content.Context;

import com.example.andre.particles.R;

import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Andrei Nikitin
 */
public class HeightMapShaderProgram extends ShaderProgram{
    private final int uTextureUnitLocation;
    private final int uVectorToLightLocation;
    private final int uMVMatrixLocation;
    private final int uITMVMatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uPointLightPositionsLocation;
    private final int uPointLightColorsLocation;
    private final int uParticlesRatio;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private final int aNormalLocation;

    public HeightMapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);

        uMVMatrixLocation = glGetUniformLocation(programId, U_MV_MATRIX);
        uITMVMatrixLocation = glGetUniformLocation(programId, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(programId, U_MVP_MATRIX);
        uPointLightColorsLocation = glGetUniformLocation(programId, U_POINT_LIGHT_COLORS);
        uPointLightPositionsLocation = glGetUniformLocation(programId, U_POINT_LIGHT_POSITIONS);
        uTextureUnitLocation = glGetUniformLocation(programId, U_TEXTURE_UNIT);
        uVectorToLightLocation = glGetUniformLocation(programId, U_VECTOR_TO_LIGHT);
        uParticlesRatio = glGetUniformLocation(programId, U_PARTICLES_RATIO);


        aPositionLocation = glGetAttribLocation(programId, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(programId, A_TEXTURE_COORDINATES);
        aNormalLocation = glGetAttribLocation(programId, A_NORMAL);
    }

    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors,
                            int textureId,
                            float particlesRatio){
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uITMVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);

        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);

        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0);
        glUniform1f(uParticlesRatio, particlesRatio);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 1);
    }

    public int getPositionAttribLocation(){
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttribLocation(){
        return aTextureCoordinatesLocation;
    }

    public int getNormalAttribLocation() {
        return aNormalLocation;
    }
}
