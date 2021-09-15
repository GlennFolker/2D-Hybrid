package hybrid;

import arc.*;
import arc.Files.*;
import arc.backend.sdl.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g3d.*;
import arc.graphics.gl.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

import static arc.Core.*;

public class Hybrid implements ApplicationListener{
    public Camera3D cam3D;

    public int rectSize = 2;
    public int tileSize = 32;
    public TextureRegion dim2;
    public Texture dim3;

    public Mesh model;
    public HShader shader;
    public float yScl = 5f;

    public Vec3 pos = new Vec3();
    public Mat3D trns = new Mat3D();
    public Quat q1 = new Quat(), q2 = new Quat();

    public static void main(String[] args){
        try{
            new SdlApplication(new Hybrid(), new SdlConfig(){{
                title = "Hybrid";
                maximized = true;
                depth = 16;

                setWindowIcon(FileType.internal, Mathf.chance(0.5) ? "2d.png" : "3d.png");
            }});
        }catch(Throwable t){
            Log.err(Strings.getFinalCause(t));
        }
    }

    @Override
    public void init(){
        camera = new Camera();
        cam3D = new Camera3D();
        batch = new SpriteBatch();

        dim2 = new TextureRegion(new Texture(files.internal("2d.png")){{ setFilter(TextureFilter.linear); }});
        dim3 = new Texture(files.internal("3d.png")){{ setFilter(TextureFilter.linear); }};

        model = new Mesh(true, Model.vertices.length, Model.indices.length, VertexAttribute.position3, VertexAttribute.normal, VertexAttribute.texCoords);
        model.setVertices(Model.vertices);
        model.setIndices(Model.indices);

        shader = new HShader();
    }

    @Override
    public void update(){
        Time.update();

        float speed = Time.delta * 5f;
        if(input.keyDown(KeyCode.w)) pos.y += speed;
        if(input.keyDown(KeyCode.a)) pos.x -= speed;
        if(input.keyDown(KeyCode.s)) pos.y -= speed;
        if(input.keyDown(KeyCode.d)) pos.x += speed;

        camera.position.set(pos);

        float scl = 1f;
        camera.resize(graphics.getWidth() / scl, graphics.getHeight() / scl);

        cam3D.position.set(camera.position.x, camera.position.y, cam3D.far / 2f);
        cam3D.resize(camera.width, camera.height);
        cam3D.update();

        Gl.depthMask(false);
        graphics.clear(Color.black);

        Draw.proj(camera);
        for(int x = -rectSize; x <= rectSize; x++){
            for(int y = -rectSize; y <= rectSize; y++){
                Draw.rect(dim2, x * tileSize, y * tileSize);
            }
        }

        Draw.flush();

        Gl.depthMask(true);
        Gl.clear(Gl.depthBufferBit);

        Gl.enable(Gl.depthTest);

        trns.set(
            Tmp.v31.set(0f, 0f, 0f),
            q1.set(Vec3.Z, Angles.angle(0f, 0f, input.mouseWorldX(), input.mouseWorldY()) - 90f)
                .mul(q2.set(Vec3.Y, Time.time * yScl)),
            Tmp.v32.set(4f, 4f, 4f)
        );

        shader.trns = trns;
        shader.texture = dim3;
        shader.camera = cam3D;

        shader.bind();
        shader.apply();

        model.render(shader, Gl.triangles);

        Gl.disable(Gl.depthTest);
    }

    public static class HShader extends Shader{
        public Mat3D trns;
        public Camera3D camera;
        public Texture texture;

        public HShader(){
            super(files.internal("g3d.vert"), files.internal("g3d.frag"));
        }

        @Override
        public void apply(){
            setUniformMatrix4("u_proj", camera.combined.val);
            setUniformMatrix4("u_trans", trns.val);

            texture.bind(0);
            setUniformi("u_texture", 0);

            setUniformf("u_camPos", camera.position);
            setUniformf("u_res", camera.width, camera.height);
            setUniformf("u_scl", graphics.getWidth() / camera.width);

            // I don't know where this value came from. I literally just did trial-and-error so many times that I came
            // up with this value because some positions misbehaved, or maybe I just suck at math, or both. I don't
            // really care, if it works it works.
            setUniformf("u_zscl", 2.0415f);
        }
    }
}
