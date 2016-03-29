#version 400 core

in vec3 vPos;
in vec2 vTex;

out vec2 out_coord;

uniform mat4 MVP;

void main(void) {
	gl_Position = MVP * vec4(vPos, 1.0);
	out_coord = vec2(vTex);
}