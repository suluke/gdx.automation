#ifdef GL_ES
	precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform vec3 u_mouseEvs[100];

void main() {
	gl_FragColor = vec4(0.f, 0.f, 0.f, 0.f);
	vec4 color = vec4(1.f, 1.f, 1.f, 1.f);
	float PHASES = 4.f;
	int hits = 0;
	for(int i = 0; i < 100; i++) {
		float dist = distance(u_mouseEvs[i].xy, gl_FragCoord.xy);
		if (dist <= u_mouseEvs[i].z) {
			float distNrml = dist / u_mouseEvs[i].z;
			float distWeighted = 2.f / (-distNrml - 1.f) + 2.f;
			float weight = (1.f - distNrml) * sin((PHASES - 1.f) * 2.f * 3.141f * distWeighted);
			gl_FragColor += weight * vec4(1.f, 1.f, 1.f, 1.f);
			hits++;
		}
	}
	gl_FragColor /= float(hits);
}
