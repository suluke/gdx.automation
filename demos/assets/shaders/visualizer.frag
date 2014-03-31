#ifdef GL_ES
	precision lowp float;
#endif
const int MAX_EVENTS = 100;
const float MAX_EXTENT = 100.f;
const float PHASES = 4.f;

varying vec4 v_color;
varying vec2 v_texCoords;
uniform vec3 u_mouseEvs[MAX_EVENTS];

void main() {
	gl_FragColor = vec4(0.f, 0.f, 0.f, 0.f);
	vec4 color = vec4(1.f, 1.f, 1.f, 1.f);
	int hits = 0;
	for(int i = 0; i < MAX_EVENTS; i++) {
		float dist = distance(u_mouseEvs[i].xy, gl_FragCoord.xy);
		if (dist <= u_mouseEvs[i].z) {
			float distNrml = dist / u_mouseEvs[i].z;
			// 1-(x-1)^2
			float distInterpol = 1.f - (distNrml - 1.f) * (distNrml - 1.f);
			// 2/(-x-1)+2
			// float distInterpol = 2.f / (-distNrml - 1.f) + 2.f;
			float ageFactor = 1.f - u_mouseEvs[i].z / MAX_EXTENT;
			float distFactor = 1.f - distNrml;
			float wave = sin(((PHASES - 0.5f) * 2.f + 1.f) * 3.141f * distInterpol);
			float weight = ageFactor * distFactor * wave;
			gl_FragColor += weight * vec4(1.f, 1.f, 1.f, 1.f);
			hits++;
		}
	}
	gl_FragColor *= 3f / float(hits);
}
