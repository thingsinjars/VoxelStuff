#version 400 core

in vec2 out_coord;

out vec4 out_color;

uniform sampler2D texture0;

uniform vec3 color;

void main(void) {
	out_color = texture(texture0, out_coord) * vec4(color, 1);
}