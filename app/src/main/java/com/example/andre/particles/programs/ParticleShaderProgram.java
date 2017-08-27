package com.example.andre.particles.programs;

import android.content.Context;

import com.example.andre.particles.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Andrei Nikitin
 */
public class ParticleShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int uTimeLocation;
    private final int uTextureUnitLocation;

    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionLocation;
    private final int aParticleStartTimeLocation;

    public ParticleShaderProgram(Context context) {
        super(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader);

        uMatrixLocation = glGetUniformLocation(programId, U_MATRIX);
        uTimeLocation = glGetUniformLocation(programId, U_TIME);
        uTextureUnitLocation = glGetUniformLocation(programId, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(programId, A_POSITION);
        aColorLocation = glGetAttribLocation(programId, A_COLOR);
        aDirectionLocation = glGetAttribLocation(programId, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(programId, A_PARTICLE_START_TIME);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttribLocation() {
        return aPositionLocation;
    }

    public int getColorAttribLocation() {
        return aColorLocation;
    }

    public int getDirectionAttribLocation() {
        return aDirectionLocation;
    }

    public int getParticleStartTimeAttribLocation() {
        return aParticleStartTimeLocation;
    }
}
