#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
//attributes from vertex shader
varying LOWP vec4 vColor;
varying vec2 vTexCoord0;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_lightmap;
uniform vec2 resolution;
uniform float transparent;

void main() {
	vec2 pos = vec2(gl_FragCoord.x,resolution.y - gl_FragCoord.y)/ resolution.xy;
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord0);
	//vec4 BaseMapTexture = texture2D(u_basemap, pos);
	vec4 LightMap = texture2D(u_lightmap,pos);
	
	if (transparent > 0.0) {
		gl_FragColor = vColor * vec4(DiffuseColor.rgb*LightMap.r,DiffuseColor.a);
	} 
	else {
		if (LightMap.r >= 0.3) {
			gl_FragColor = vColor*vec4(DiffuseColor.rgb*LightMap.r,DiffuseColor.a);
		 }
		 else {
			gl_FragColor = vColor*vec4(0.0,0.0,0.0,0.0);
		 }
		 //gl_FragColor = vColor*vec4(DiffuseColor.rgb,DiffuseColor.a);
	}

}