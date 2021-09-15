attribute vec4 a_position;
attribute vec2 a_texCoord0;

varying vec2 v_texCoords;

uniform mat4 u_proj;
uniform mat4 u_trans;

uniform vec3 u_camPos;
uniform vec2 u_res;
uniform vec2 u_scl;

uniform float u_zscl;

void main(){
    v_texCoords = a_texCoord0;

    mat4 trns = u_trans;

    vec4 translation = vec4(trns[3][0], trns[3][1], trns[3][2], 0.0);
    trns[3][0] = u_camPos.x;
    trns[3][1] = u_camPos.y;

    trns[0][0] = trns[0][0] * u_scl.x;
    trns[1][1] = trns[1][1] * u_scl.y;
    trns[2][2] = trns[2][2] * (u_scl.x + u_scl.y) / 2.0;

    vec4 pos = u_proj * trns * a_position;
    pos -= vec4((u_camPos.xy - translation.xy) * pos.z * u_zscl / u_res, 0.0, 0.0);

    gl_Position = pos;
}
