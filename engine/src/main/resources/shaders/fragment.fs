#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;

vec4 ambientColor;
vec4 diffuseColor;
vec4 specularColor;

void setupColors(Material mat, vec2 texCoords) {
    if (mat.hasTexture == 1) {
        ambientColor = texture(textureSampler, texCoords);
    } else {
        ambientColor = mat.ambient + mat.specular + mat.diffuse + mat.reflectance;
    }
}

void main() {
    setupColors(material, fragTextureCoord);
    fragColor = ambientColor * vec4(ambientLight, 1);
}