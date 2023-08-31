#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;
uniform sampler2D Sampler5;
uniform sampler2D Sampler6;
uniform sampler2D Sampler7;
uniform int texcount;

uniform vec4 ColorModulator;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if(texcount >= 2){
        vec4 col1 = texture(Sampler1, texCoord0);
        color.rgb = mix(color.rgb, col1.rgb, col1.a);
        color.a = color.a + (col1.a * (1.0 - color.a));
    }
    if(texcount >= 3){
        vec4 col2 = texture(Sampler2, texCoord0);
        color.rgb = mix(color.rgb, col2.rgb, col2.a);
        color.a = color.a + (col2.a * (1.0 - color.a));
    }
    if(texcount >= 4){
        vec4 col3 = texture(Sampler3, texCoord0);
        color.rgb = mix(color.rgb, col3.rgb, col3.a);
        color.a = color.a + (col3.a * (1.0 - color.a));
    }
    if(texcount >= 5){
        vec4 col4 = texture(Sampler4, texCoord0);
        color.rgb = mix(color.rgb, col4.rgb, col4.a);
        color.a = color.a + (col4.a * (1.0 - color.a));
    }
    if(texcount >= 6){
        vec4 col5 = texture(Sampler5, texCoord0);
        color.rgb = mix(color.rgb, col5.rgb, col5.a);
        color.a = color.a + (col5.a * (1.0 - color.a));
    }
    if(texcount >= 7){
        vec4 col6 = texture(Sampler6, texCoord0);
        color.rgb = mix(color.rgb, col6.rgb, col6.a);
        color.a = color.a + (col6.a * (1.0 - color.a));
    }
    if(texcount >= 8){
        vec4 col7 = texture(Sampler7, texCoord0);
        color.rgb = mix(color.rgb, col7.rgb, col7.a);
        color.a = color.a + (col7.a * (1.0 - color.a));
    }
    if (color.a <= 0.0) {
        discard;
    }
    fragColor = color * ColorModulator;
}
