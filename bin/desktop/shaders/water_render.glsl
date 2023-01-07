#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture,iChannel0;
varying vec4 vColor;
varying vec2 vTexCoord0;
uniform vec2 iResolution,iMouse,iDisp;
uniform float iTime;

// comment to disable cubemap reflections
// #define REFLECTIONS

float uFreqY = 20.0;
float uFreqX = 20.0;
float uSpeed = 3.0;
float uAmplitude = 0.004;

void main()
{
	vec2 uv = vTexCoord0;
	vec4 tex = texture(u_texture,uv);
	if(tex.a<0.1) 
	{	
		gl_FragColor = vec4(0);
	}
	else {
		vec2 water_uv = 1*((gl_FragCoord.xy+iDisp*1)/iResolution.xy);
		//water_uv.x *= (iResolution.x/iResolution.y);
		vec2 new_uv = uv + vec2(sin(uFreqY*water_uv.y + uFreqX*water_uv.x + uSpeed*iTime) * uAmplitude);
		gl_FragColor = texture(u_texture,new_uv)*1;
		gl_FragColor += texture(u_texture,uv)*0;
		gl_FragColor += vec4(new_uv,0,0)*0;
	}
		

}