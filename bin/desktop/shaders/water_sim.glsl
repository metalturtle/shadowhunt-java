// Make this a smaller number for a smaller timestep.
// Don't make it bigger than 1.4 or the universe will explode.

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying vec4 vColor;
varying vec2 vTexCoord0;
const float delta = 1.0;
uniform sampler2D u_texture;
uniform vec2 iResolution;
uniform vec3 iMouse;


void main()
{   
    float pressure = texelFetch(u_texture, ivec2(gl_FragCoord), 0).x;
    float pVel = texelFetch(u_texture, ivec2(gl_FragCoord), 0).y;

    float p_right = texelFetch(u_texture, ivec2(gl_FragCoord) + ivec2(1, 0), 0).x;
    float p_left = texelFetch(u_texture, ivec2(gl_FragCoord) + ivec2(-1, 0), 0).x;
    float p_up = texelFetch(u_texture, ivec2(gl_FragCoord) + ivec2(0, 1), 0).x;
    float p_down = texelFetch(u_texture, ivec2(gl_FragCoord) + ivec2(0, -1), 0).x;
    
    // Change values so the screen boundaries aren't fixed.
    if (gl_FragCoord.x == 0.5) p_left = p_right;
    if (gl_FragCoord.x == iResolution.x - 0.5) p_right = p_left;
	if (gl_FragCoord.y == 0.5) p_down = p_up;
    if (gl_FragCoord.y == iResolution.y - 0.5) p_up = p_down;

    // Apply horizontal wave function
    pVel += delta * (-2.0 * pressure + p_right + p_left) / 4.0;
    // Apply vertical wave function (these could just as easily have been one line)
    pVel += delta * (-2.0 * pressure + p_up + p_down) / 4.0;
    
    // Change pressure by pressure velocity
    pressure += delta * pVel;
    
    // "Spring" motion. This makes the waves look more like water waves and less like sound waves.
    pVel -= 0.005 * delta * pressure;
    
    // Velocity damping so things eventually calm down
    pVel *= 1.0 - 0.002 * delta;
    
    // Pressure damping to prevent it from building up forever.
    pressure *= 0.999;
    
    //x = pressure. y = pressure velocity. Z and W = X and Y gradient
    gl_FragColor.xyzw = vec4(pressure, pVel, (p_right - p_left) / 2.0, (p_up - p_down) / 2.0);
    
    
    if (iMouse.z > 1.0) {
        float dist = distance(gl_FragCoord, iMouse.xy);
        if (dist <= 20.0) {
            gl_FragColor.x += 1.0 - dist / 20.0;
        }
    }
}