package com.example.andre.particles.objects;

import android.graphics.Color;
import android.opengl.GLES20;

import com.example.andre.particles.Constants;
import com.example.andre.particles.data.VertexArray;
import com.example.andre.particles.programs.ParticleShaderProgram;

import static com.example.andre.particles.util.Geometry.Point;
import static com.example.andre.particles.util.Geometry.Vector;

/**
 * Created by Andrei Nikitin
 */
public class ParticleSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT
            + VECTOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COMPONENT_COUNT;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constants.BYTES_PER_FLOAT;

    private final float[] particles;
    private final VertexArray vertexArray;
    private final int maxParticleCount;
    private final long globalStartTime;

    private int currentParticleCount;
    private int nextParticle;

    public ParticleSystem(int maxParticleCount, long globalStartTime){
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
        this.globalStartTime = globalStartTime;
    }

    public void addParticle(Point position, int color, Vector direction, float particleStartTime){
        int particleOffset = 0;
        boolean aboveTheGround = true;

//        for(int i = 0; i < currentParticleCount; i++){
//            int currPosition = i * TOTAL_COMPONENT_COUNT;
//            aboveTheGround = isParticleAboveTheGround(
//                    particles[currPosition + 2],
//                    particles[currPosition + 7],
//                    particles[currPosition + 9]);
//
//            if(!aboveTheGround){
//                particleOffset = currPosition;
//                break;
//            }
//        }

        if(aboveTheGround) {
            particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
            nextParticle++;

            if(currentParticleCount < maxParticleCount){
                currentParticleCount++;
            }

            if(nextParticle == maxParticleCount){
                nextParticle = 0;
            }
        }

        int currentOffset = particleOffset;

        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;

        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;

        particles[currentOffset] = particleStartTime;

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(ParticleShaderProgram program){
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(
                dataOffset,
                program.getPositionAttribLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );

        dataOffset += POSITION_COMPONENT_COUNT;
        vertexArray.setVertexAttribPointer(
                dataOffset,
                program.getColorAttribLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        );

        dataOffset += COLOR_COMPONENT_COUNT;
        vertexArray.setVertexAttribPointer(
                dataOffset,
                program.getDirectionAttribLocation(),
                VECTOR_COMPONENT_COUNT,
                STRIDE
        );

        dataOffset += VECTOR_COMPONENT_COUNT;
        vertexArray.setVertexAttribPointer(
                dataOffset,
                program.getParticleStartTimeAttribLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);
    }

    public float getParticlesCountToMaxRatio(){
        return (float) currentParticleCount / (float) maxParticleCount / 5;
    }

    private boolean isParticleAboveTheGround(float particleY,
                                             float normalY,
                                             float particleStartTime){
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        float elapsedTime = currentTime - particleStartTime;
        float gravityFactor = elapsedTime * elapsedTime / 8.0f;
        float y = particleY + (normalY * elapsedTime) - gravityFactor;

        return y > 0.0f;
    }
}
