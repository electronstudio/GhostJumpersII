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

void main() {
	vec4 texColor = texture2D(u_texture, vTexCoord);

    if(vColor.a==0.0 || texColor.a == 0.0){
        gl_FragColor = texColor;
    } else{
        gl_FragColor = vColor; //vec4(1.0, 1.0, 1.0, 1.0);
    }
}