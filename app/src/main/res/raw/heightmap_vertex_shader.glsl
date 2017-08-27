
uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;
uniform mat4 u_MVPMatrix;

uniform vec3 u_VectorToLight;
uniform vec4 u_PointLightPositions[3];
uniform vec3 u_PointLightColors[3];
uniform float u_ParticlesRatio;

attribute vec3 a_Normal;
attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;
varying float v_AmbientLightingModifier;
varying float v_DirectionalLightingModifier;
varying vec3 v_PointLightingModifier;

vec3 materialColor;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;

float getAmbientLighting();
float getDirectionalLighting();
vec3 getPointLighting();

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    eyeSpacePosition = u_MVMatrix * a_Position;

    eyeSpaceNormal = normalize(vec3(u_IT_MVMatrix * vec4(a_Normal, 0.0)));

    v_AmbientLightingModifier = getAmbientLighting();
    v_DirectionalLightingModifier = getDirectionalLighting();
    v_PointLightingModifier = getPointLighting() * u_ParticlesRatio;

    gl_Position = u_MVPMatrix * a_Position;
}

float getAmbientLighting(){
    return 0.1;
}

float getDirectionalLighting(){
    return 0.3 * max(dot(eyeSpaceNormal, u_VectorToLight), 0.0);
}

vec3 getPointLighting(){
    vec3 lightingSum = vec3(0.0);

    for(int i = 0; i < 3; i++){
        vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);
        float distance = length(toPointLight);
        toPointLight = normalize(toPointLight);

        float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
        lightingSum += (u_PointLightColors[i] * 5.0 * cosine) / distance;
    }

    return lightingSum;
}