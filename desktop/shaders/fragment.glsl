
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
uniform sampler2D u_normals;   //normal map
uniform sampler2D u_lightmask;
uniform sampler2D u_lightmap;

//values used for shading algorithm...
uniform vec2 Resolution;         //resolution of screen
//uniform vec3 LightPos[8];           //light position, normalized
//uniform vec2 CameraPosition;
uniform vec3 LightPos[8];
uniform float LightActive[8];
uniform LOWP vec4 LightColor[8];
// uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity
uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity 
uniform vec3 Falloff;            //attenuation coefficients

float PHI = 1.61803398874989484820459;  // Φ = Golden Ratio   

float random(vec2 p){return fract(cos(dot(p,vec2(23.14069263277926,2.665144142690225)))*12345.6789);}

void main() {
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord0);
	//RGB of our normal map
	vec3 NormalMap = texture2D(u_normals, vTexCoord0).rgb;
	vec4 LightMask = texture2D(u_lightmask,vTexCoord0);
	vec4 LightMap = texture2D(u_lightmap, vTexCoord0);
	int i = 0;
	float ambientVal = LightMask.g;
	vec3 FinalColor = vec3(0);
	//FinalColor = DiffuseColor.rgb + 0*LightMap;
	// FinalColor = vec4(0,1,0,0);
	vec3 N = normalize(NormalMap * 2.0 - 1.0);
	vec3 L = normalize(vec3(0,0,1));
	vec3 Diffuse = LightMap.rgb*max(dot(L, N), 0.0);
	FinalColor = DiffuseColor.rgb*Diffuse;
	if (LightMask.r > 0) {
		for (i = 0; i < 3; i++) {
			if(LightActive[i] == 0)
				continue;
				
				//The delta position of light
			vec3 LightDir = vec3(LightPos[i].xy - (gl_FragCoord.xy / Resolution.xy), LightPos[i].z);
			
			//Correct for aspect ratio
			LightDir.x *= Resolution.x / Resolution.y;
			
			//Determine distance (used for attenuation) BEFORE we normalize our LightDir
			float D = length(LightDir);
			
			//normalize our vectors
			N = normalize(NormalMap * 2.0 - 1.0);
			L = normalize(LightDir);
			
			//Pre-multiply light color with intensity
			//Then perform "N dot L" to determine our diffuse term
			Diffuse = LightColor[i].rgb*max(dot(L, N), 0.0);
			//vec3 Diffuse = N;
			float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );
			//vec3 Diffuse = vec3(length(Attenuation));

			//pre-multiply ambient color with intensity
			vec3 Ambient = AmbientColor.rgb * AmbientColor.a;
			
			//calculate attenuation
			//the calculation which brings it all together
			vec3 Intensity =  Diffuse*Attenuation;
			// FinalColor +=DiffuseColor.rgb*Intensity;
			FinalColor += DiffuseColor.rgb*Intensity;
		}
	}

	gl_FragColor = vColor * vec4(FinalColor.rgb, DiffuseColor.a);
}
