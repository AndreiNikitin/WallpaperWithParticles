package com.example.andre.particles.wallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.example.andre.particles.ParticlesRenderer;

/**
 * Created by Andrei Nikitin
 */
public class GLWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine{
        private GLEngine.WallpaperGLSurfaceView surfaceView;
        private ParticlesRenderer renderer;
        private boolean rendererSet;

        @Override
        public void onOffsetsChanged(final float xOffset, final float yOffset,
                                     float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            surfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    renderer.handleOffsetsChanged(xOffset, yOffset);
                }
            });
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            surfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
            renderer = new ParticlesRenderer(GLWallpaperService.this);

            ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
            if(manager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000){
                surfaceView.setEGLContextClientVersion(2);
                if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                    surfaceView.setPreserveEGLContextOnPause(true);
                }
                surfaceView.setRenderer(renderer);
                rendererSet = true;
            } else {
                Toast.makeText(GLWallpaperService.this,
                        "This device does nor support OpenGL ES 2.0.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    surfaceView.onResume();
                } else {
                    surfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            surfaceView.onWallpaperDestroy();
        }

        class WallpaperGLSurfaceView extends GLSurfaceView{
            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy(){
                super.onDetachedFromWindow();
            }
        }
    }
}
