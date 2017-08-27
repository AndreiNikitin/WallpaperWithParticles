package com.example.andre.particles.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by Andrei Nikitin
 */
public final class ShaderUtils {
    private static final String LOG_TAG = ShaderUtils.class.getSimpleName();

    public static int compileVertexShader(String source){
        return compileShader(GL_VERTEX_SHADER, source);
    }

    public static int compileFragmentShader(String source){
        return compileShader(GL_FRAGMENT_SHADER, source);
    }

    public static boolean validateProgram(int programId){
        glValidateProgram(programId);

        final int validateStatus[] = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(LOG_TAG, "Result of validating program: " + validateStatus[0]
                            + System.lineSeparator() + glGetProgramInfoLog(programId));

        return validateStatus[0] != 0;
    }

    private static int compileShader(int type, String source){
        final int shaderId = glCreateShader(type);

        if(shaderId == 0){
            Log.d(LOG_TAG, "Could not create new shader.");
            return shaderId;
        }

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        final int compileStatus[] = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);

        Log.d(LOG_TAG, "Results of compiling source:" + System.lineSeparator() +
                shaderId + System.lineSeparator() +
                glGetShaderInfoLog(shaderId));

        if(compileStatus[0] == 0){
            glDeleteShader(shaderId);
            Log.d(LOG_TAG, "Compilation of shader failed");
            return 0;
        }

        return shaderId;
    }

    public static int linkProgram(int vertexShader, int fragmentShader){
        final int programId = glCreateProgram();

        if(programId == 0){
            Log.d(LOG_TAG, "Could not create new program");
            return programId;
        }

        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        final int linkStatus[] = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);

        Log.d(LOG_TAG, "Results of linking program:" + System.lineSeparator() +
                programId + System.lineSeparator() +
                glGetProgramInfoLog(programId));

        if(linkStatus[0] == 0){
            glDeleteProgram(programId);
            Log.d(LOG_TAG, "Linking of program failed.");
            return 0;
        }

        return programId;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource){
        int program;

        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        program = linkProgram(vertexShader, fragmentShader);
        validateProgram(program);

        return program;
    }
}
