#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
//texture 0
uniform sampler2D u_texture;

//"in" attributes from our vertex shader
varying LOWP vec4 vColor;
varying vec2 vTexCoord;

float gold_noise(in vec2 coordinate, in float seed)
{
    return fract(sin(dot(coordinate*seed, vec2(12.9898989898, 78.233333)))*43758.54535353);
}


void main() {
	vec4 texColor = texture2D(u_texture, vTexCoord);

    if(vColor.a==0.0 || texColor.a == 0.0){
        gl_FragColor = texColor; // vec4(gold_noise(vTexCoord, 1.0));
    } else{
        gl_FragColor = vColor; //vec4(1.0, 1.0, 1.0, 1.0);
    }
}

