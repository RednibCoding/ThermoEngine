package org.thermo.core;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.thermo.core.entity.Model;
import org.thermo.core.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {

    // Vertex array objects
    private List<Integer> vaos = new ArrayList<>();
    // Vertex buffer objects
    private List<Integer> vbos = new ArrayList<>();

    private List<Integer> textures = new ArrayList<>();

    public Model loadObjModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line : lines) {
            String[] tokens = line.split("\\s");
            switch (tokens[0]) {
                case "v":
                    // Vertices
                    Vector3f verts = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verts);
                    break;
                case "vt":
                    // Vertex textures
                    Vector2f tex = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(tex);
                    break;
                case "vn":
                    // Vertex normals
                    Vector3f vertNormal = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(vertNormal);
                    break;
                case "f":
                    // Faces
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArray[i * 3] = pos.x;
            verticesArray[i * 3 + 1] = pos.y;
            verticesArray[i * 3 + 2] = pos.z;
            i++;
        }

        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArray, texCoordArr, normalArr, indicesArr);
    }

    private static void processVertex(int pos, int texCoord, int normal,
                                      List<Vector2f> texCoordList,
                                      List<Vector3f> normalList,
                                      List<Integer> indicesList,
                                      float[] texCoordArr,
                                      float[] normalArr) {
        indicesList.add(pos);
        if (texCoord >= 0) {
            Vector2f texCoordVec = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if (normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] tokenLine = token.split("/");
        int length = tokenLine.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(tokenLine[0]) -1;
        if (length > 1) {
            String textCoord = tokenLine[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) -1 : -1;
            if (length > 2) {
                normal = Integer.parseInt(tokenLine[2]) - 1;
            }
        }
        Vector3i face = new Vector3i(pos, coords, normal);
        faces.add(face);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        int id = createVao();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        storeDataInAttribList(2, 3, normals);
        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String filename) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            int numChannels = 4;

            buffer = STBImage.stbi_load(filename, w, h ,channels, numChannels);
            if (buffer == null) {
                throw new Exception("Image File " + filename + " not loaded " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVao() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void free() {
        for(int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for(int tex : textures)
            GL11.glDeleteTextures(tex);

    }
}
