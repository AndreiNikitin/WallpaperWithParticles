package com.example.andre.particles;

import android.app.ActivityManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        final ParticlesRenderer renderer = new ParticlesRenderer(this);

        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        if(manager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000){
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setPreserveEGLContextOnPause(true);
            glSurfaceView.setRenderer(renderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does nor support OpenGL ES 2.0.", Toast.LENGTH_SHORT).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new OnTouchListener() {
            float previousX, previousY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event != null){
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        previousX = event.getX();
                        previousY = event.getY();
                    } else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        final float dx = event.getX() - previousX;
                        final float dy = event.getY() - previousY;

                        previousY = event.getY();
                        previousX = event.getX();

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(dx, dy);
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rendererSet){
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(rendererSet){
            glSurfaceView.onResume();
        }
    }
}
