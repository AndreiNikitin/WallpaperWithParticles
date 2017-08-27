precision mediump float;

uniform sampler2D u_TextureUnit;

varying vec2 v_TextureCoordinates;
varying float v_AmbientLightingModifier;
varying float v_DirectionalLightingModifier;
varying vec3 v_PointLightingModifier;

void main() {
    vec4 pixelColor = texture2D(u_TextureUnit, v_TextureCoordinates);
    gl_FragColor = pixelColor * v_AmbientLightingModifier +
                    pixelColor * v_DirectionalLightingModifier +
                    vec4(v_PointLightingModifier, 1.0);
}