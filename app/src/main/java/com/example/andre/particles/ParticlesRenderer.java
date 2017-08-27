package com.example.andre.particles;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.example.andre.particles.objects.HeightMap;
import com.example.andre.particles.objects.ParticleFireworksExplosion;
import com.example.andre.particles.objects.ParticleShooter;
import com.example.andre.particles.objects.ParticleSystem;
import com.example.andre.particles.objects.SkyBox;
import com.example.andre.particles.programs.HeightMapShaderProgram;
import com.example.andre.particles.programs.ParticleShaderProgram;
import com.example.andre.particles.programs.SkyBoxShaderProgram;
import com.example.andre.particles.util.Geometry.Point;
import com.example.andre.particles.util.Geometry.Vector;
import com.example.andre.particles.util.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Andrei Nikitin
 */
public class ParticlesRenderer implements GLSurfaceView.Renderer {
    private static final String LOG_TAG = ParticlesRenderer.class.getSimpleName();
    //*3500 - 5 - 30*    proportion to calculate changes in this three attributes.
    private static final int ALL_PARTICLES_COUNT = 3500;
    private static final int PARTICLES_TO_THROW_PER_SHOOTER = 5;
    private static final int MAX_FRAME_RATE = 30;

    private final Context context;

    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkyBox = new float[16];
    private final float[] projectionMatrix = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];

    private final float[] vectorToLight = {0.30f, 0.35f, -0.89f, 0f};
    private final float[] pointLightPositions = new float[]{
            -1f, 1f, 0f, 1f,
             0f, 1f, 0f, 1f,
             1f, 1f, 0f, 1f,
    };

    private final float[] pointLightColors = new float[]{
            1.00f, 0.20f, 0.02f,
            0.02f, 1.00f, 0.02f,
            0.02f, 0.20f, 1.00f,
    };

//    private final float hsv[] = {0f, 1f, 1f};

    private ParticleShaderProgram particleProgram;
    private SkyBoxShaderProgram skyBoxProgram;
    private HeightMapShaderProgram heightMapProgram;

    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private ParticleFireworksExplosion fireworksExplosion;
    private SkyBox skyBox;
    private HeightMap heightMap;

    private long globalStartTime;
    private int particleTexture;
    private int skyBoxTexture;
    private int terrainFieldTexture;
    private float xRotation, yRotation;
    private float xOffset, yOffset;
    private long frameStartTimeMs;
    private long startTimeMs;
    private int frameCount;

    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        particleProgram = new ParticleShaderProgram(context);
        skyBoxProgram = new SkyBoxShaderProgram(context);
        heightMapProgram = new HeightMapShaderProgram(context);

        globalStartTime = System.nanoTime();
        particleSystem = new ParticleSystem(ALL_PARTICLES_COUNT, globalStartTime);

        final Vector particleDirection = new Vector(0f, 0.5f, 0f);
        final float speedVariance = 1f;
        final float angleVarianceInDegrees = 5f;

        redParticleShooter = new ParticleShooter(
                new Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance
        );

        greenParticleShooter = new ParticleShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(50, 255, 50),
                angleVarianceInDegrees,
                speedVariance
        );

        blueParticleShooter = new ParticleShooter(
                new Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance
        );

        fireworksExplosion = new ParticleFireworksExplosion();

        skyBox = new SkyBox();
        heightMap = new HeightMap(((BitmapDrawable)context.getResources()
                .getDrawable(R.drawable.heightmap)).getBitmap());

        particleTexture = TextureUtils.loadTexture(context, R.drawable.particle_texture);
        skyBoxTexture = TextureUtils.loadCubeMap(
                context,
                new int[]{
                        R.drawable.night_left, R.drawable.night_right,
                        R.drawable.night_bottom, R.drawable.night_top,
                        R.drawable.night_front, R.drawable.night_back}
        );
        terrainFieldTexture = TextureUtils.loadTexture(context, R.drawable.terrain_field);

//        Enables gl_PointSize in vertex shader
//        glEnable(0x8642);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width / (float) height, 1f, 100f);

        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        limitFrameRate(MAX_FRAME_RATE);
        logFrameRate();

        drawHeightMap();
        drawSkyBox();
        drawParticles();
    }

    private void logFrameRate() {
        long elapsedRealTimeMs = SystemClock.elapsedRealtime();
        double elapsedSeconds = (elapsedRealTimeMs - startTimeMs) / 1000.0f;

        if(elapsedSeconds >= 1.0){
            Log.d(LOG_TAG, frameCount / elapsedSeconds + " fps");
            startTimeMs = SystemClock.elapsedRealtime();
            frameCount = 0;
        }
        frameCount++;
    }

    private void limitFrameRate(int framesPerSecond) {
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
        long expectedFrameTimeMs = 1000 / framesPerSecond;
        long timeToSleepMs = expectedFrameTimeMs - elapsedFrameTimeMs;

        if(timeToSleepMs > 0){
            SystemClock.sleep(timeToSleepMs);
        }

        frameStartTimeMs = SystemClock.elapsedRealtime();
    }


    private void drawHeightMap() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();

        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] pointPositionsInEyeSpace = new float[12];
        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        Matrix.multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0);
        Matrix.multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4);
        Matrix.multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8);

        heightMapProgram.useProgram();
        heightMapProgram.setUniforms(modelViewMatrix, it_modelViewMatrix,
                modelViewProjectionMatrix, vectorToLightInEyeSpace,
                pointPositionsInEyeSpace, pointLightColors, terrainFieldTexture,
                particleSystem.getParticlesCountToMaxRatio());
        heightMap.bindData(heightMapProgram);
        heightMap.draw();
    }

    private void drawSkyBox() {
        Matrix.setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkyBox();

        skyBoxProgram.useProgram();
        skyBoxProgram.setUniforms(modelViewProjectionMatrix, skyBoxTexture);
        skyBox.bindData(skyBoxProgram);
        glDepthFunc(GL_LEQUAL);
        skyBox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        redParticleShooter.addParticles(particleSystem, currentTime, PARTICLES_TO_THROW_PER_SHOOTER);
        greenParticleShooter.addParticles(particleSystem, currentTime, PARTICLES_TO_THROW_PER_SHOOTER);
        blueParticleShooter.addParticles(particleSystem, currentTime, PARTICLES_TO_THROW_PER_SHOOTER);

        Matrix.setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
//
//        if(random.nextFloat() < 0.02f){
//            hsv[0] = random.nextInt(360);
//
//            fireworksExplosion.addExplosion(
//                    particleSystem,
//                    new Point(-1.0f, 3f, 0f),
//                    Color.HSVToColor(hsv),
//                    globalStartTime
//            );
//        }

        particleProgram.useProgram();
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        glDepthMask(false);
        particleSystem.draw();
        glDepthMask(true);

        glDisable(GL_BLEND);
    }

    public void handleTouchDrag(float dx, float dy) {
        xRotation += dx / 16f;
        yRotation += dy / 16f;

        if(yRotation < -90){
            yRotation = -90;
        } else if(yRotation > 90){
            yRotation = 90;
        }

        updateViewMatrices();
    }

    private void updateViewMatrices(){
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkyBox, 0, viewMatrix.length);

        Matrix.translateM(viewMatrix, 0, 0f - xOffset, -1.5f - yOffset, -5f);
    }

    private void updateMvpMatrix(){
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.invertM(tempMatrix, 0, modelViewMatrix, 0);
        Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
    }

    private void updateMvpMatrixForSkyBox(){
        Matrix.multiplyMM(tempMatrix, 0, viewMatrixForSkyBox, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    public void handleOffsetsChanged(float xOffset, float yOffset) {
        this.xOffset = (xOffset - 0.5f) * 2.5f;
        this.yOffset = (yOffset - 0.5f) * 2.5f;
        updateViewMatrices();
    }
}
