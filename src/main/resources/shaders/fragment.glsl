#version 330

in vec2 oUV;
in vec3 oNormal;

uniform sampler2D atlas;

out vec4 fragColor;

void main() {
    fragColor = texture(atlas, oUV);
}