#ifdef GL_ES
	precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform vec3 u_mouseEvs[30];

void main() {
	//~ gl_FragColor = v_color;
	//~ float val = 0.f;
	//~ for(int i = 0; i < 30; i++)
		//~ val += u_mouseEvs[i].x;
	//~ gl_FragColor = vec4(1.f + val, 1.f, 1.f, 1.f);
	gl_FragColor = vec4(1.f, 1.f, 1.f, 1.f);
}
