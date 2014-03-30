#ifdef GL_ES
	precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform vec3 u_mouseEvs[100];

void main() {
	gl_FragColor = vec4(0.f, 0.f, 0.f, 0.f);
	for(int i = 0; i < 100; i++) {
		if (distance(u_mouseEvs[i].xy, gl_FragCoord.xy) <= u_mouseEvs[i].z) {
			gl_FragColor = vec4(1.f, 1.f, 1.f, 1.f);
		}
	}
}
