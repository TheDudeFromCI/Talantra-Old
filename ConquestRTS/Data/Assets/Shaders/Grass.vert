uniform float time;
in float att_swayTolerance;
in vec3 att_offset;
in vec2 att_rotScale;
in vec3 att_color;
in vec2 att_uv;
out vec2 uv;
out vec3 color;

const float swayStrength = 0.15;

void main(){
	vec2 r = vec2(cos(att_rotScale.x), sin(att_rotScale.x));
	vec3 pos = vec3(
		gl_Vertex.x*r.x-gl_Vertex.z*r.y,
		gl_Vertex.y,
		gl_Vertex.x*r.y+gl_Vertex.z*r.x
	)*att_rotScale.y;
	vec2 swayLocations = vec2(sin(time+pos.x), cos(time+pos.z))*swayStrength*att_swayTolerance;
	pos.x += swayLocations.x;
	pos.y -= swayLocations.x+swayLocations.y;
	pos.z += swayLocations.y;
	gl_Position = gl_ModelViewProjectionMatrix*vec4(att_offset+pos, 1.0);
	uv = att_uv;
	color = att_color;
}