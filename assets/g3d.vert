attribute vec4 a_position;
attribute vec2 a_texCoord0;

varying vec2 v_texCoords;

uniform mat4 u_proj;
uniform mat4 u_trans;

uniform vec3 u_camPos;
uniform vec2 u_res;

uniform float u_scl;
uniform float u_zscl;

void main(){
    v_texCoords = a_texCoord0;

    mat4 trns = u_trans;
    vec4 translation = vec4(trns[3][0], trns[3][1], trns[3][2], 0.0);

    trns[3][0] = u_camPos.x;
    trns[3][1] = u_camPos.y;
    trns[3][2] = translation.z - u_scl * u_zscl * u_zscl;

    trns *= mat4(
        vec4(u_scl, 0.0, 0.0, 0.0),
        vec4(0.0, u_scl, 0.0, 0.0),
        vec4(0.0, 0.0, u_scl, 0.0),
        vec4(0.0, 0.0, 0.0, 1.0)
    );

    vec2 diff = u_camPos.xy - translation.xy;
    vec4 pos = u_proj * trns * a_position;

    pos -= vec4(diff * pos.z * u_zscl / u_res, 0.0, 0.0);
    gl_Position = pos;
}
