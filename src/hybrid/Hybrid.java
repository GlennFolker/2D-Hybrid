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

    public TextureRegion dim2;
    public Texture dim3;

    public Mesh model;
    public HShader shader;

    public Vec3 pos = new Vec3();
    public Mat3D trns = new Mat3D();
    public Quat quat = new Quat();

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

        dim2 = new TextureRegion(new Texture(files.internal("2d.png")));
        dim3 = new Texture(files.internal("3d.png"));

        double[] vertsRaw = new double[]{
            1.000000,  0.250000,  1.250000,  0.315981, -0.039498,  0.947943,  0.269531,  0.500000,
            0.000000, -1.750000,  1.500000,  0.315981, -0.039498,  0.947943,  0.027344,  0.023438,
            0.750000, -1.750000,  1.250000,  0.315981, -0.039498,  0.947943,  0.207031,  0.023438,
            0.200000,  0.250000,  1.450000,  0.242536, -0.000000,  0.970143,  0.078125,  0.500000,
            0.000000, -1.750000,  1.500000,  0.242536, -0.000000,  0.970143,  0.027344,  0.023438,
            1.000000,  0.250000,  1.250000,  0.242536, -0.000000,  0.970142,  0.269531,  0.500000,
            0.000000,  0.250000,  1.500000,  0.242536, -0.000000,  0.970143,  0.027344,  0.500000,
            -0.750000, -1.750000,  1.250000, -0.315981, -0.039498,  0.947943,  0.207031,  0.023438,
            0.000000, -1.750000,  1.500000, -0.315981, -0.039498,  0.947943,  0.027344,  0.023438,
            -1.000000,  0.250000,  1.250000, -0.315981, -0.039498,  0.947943,  0.269531,  0.500000,
            0.000000, -1.750000,  1.500000, -0.242536, -0.000000,  0.970143,  0.027344,  0.023438,
            0.000000,  0.250000,  1.500000, -0.242536, -0.000000,  0.970143,  0.027344,  0.500000,
            -0.200000,  0.250000,  1.450000, -0.242536, -0.000000,  0.970143,  0.078125,  0.500000,
            -1.000000,  0.250000,  1.250000, -0.242536, -0.000000,  0.970142,  0.269531,  0.500000,
            -0.750000, -1.750000,  1.250000, -0.992278, -0.124035,  0.000000,  0.695312,  0.023438,
            -1.000000,  0.250000,  1.250000, -0.992278, -0.124035,  0.000000,  0.695312,  0.500000,
            -1.000000,  0.250000,  1.000000, -0.992278, -0.124035,  0.000000,  0.636719,  0.500000,
            -0.750000, -1.750000,  1.000000, -0.992278, -0.124035,  0.000000,  0.636719,  0.023438,
            1.000000,  0.250000,  1.250000,  0.992278, -0.124035,  0.000000,  0.695312,  0.500000,
            0.750000, -1.750000,  1.250000,  0.992278, -0.124035,  0.000000,  0.695312,  0.023438,
            0.750000, -1.750000,  1.000000,  0.992278, -0.124035,  0.000000,  0.636719,  0.023438,
            1.000000,  0.250000,  1.000000,  0.992278, -0.124035,  0.000000,  0.636719,  0.500000,
            0.200000,  0.250000,  1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.074219,
            0.000000,  0.250000,  1.250000,  0.000000,  1.000000,  0.000000,  0.753906,  0.023438,
            0.000000,  0.250000,  1.500000,  0.000000,  1.000000,  0.000000,  0.816406,  0.023438,
            0.200000,  0.250000,  1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.074219,
            -0.750000, -1.750000,  1.250000,  0.000000, -1.000000,  0.000000,  0.816406,  0.449219,
            0.000000, -1.750000,  1.250000,  0.000000, -1.000000,  0.000000,  0.753906,  0.269531,
            0.000000, -1.750000,  1.500000,  0.000000, -1.000000,  0.000000,  0.816406,  0.269531,
            -0.750000, -1.750000,  1.000000,  0.000000, -1.000000,  0.000000,  0.753906,  0.449219,
            0.750000, -1.750000,  1.250000,  0.000000, -1.000000,  0.000000,  0.816406,  0.449219,
            0.750000, -1.750000,  1.000000,  0.000000, -1.000000,  0.000000,  0.753906,  0.449219,
            0.200000,  0.250000,  1.200000, -0.242536,  0.000000, -0.970143,  0.382812,  0.500000,
            0.000000, -1.750000,  1.250000, -0.242536,  0.000000, -0.970143,  0.335938,  0.023438,
            0.000000,  0.250000,  1.250000, -0.242536,  0.000000, -0.970143,  0.335938,  0.500000,
            1.000000,  0.250000,  1.000000, -0.242536,  0.000000, -0.970142,  0.574219,  0.500000,
            0.000000, -1.750000,  1.250000,  0.242536,  0.000000, -0.970143,  0.335938,  0.023438,
            -0.200000,  0.250000,  1.200000,  0.242536,  0.000000, -0.970143,  0.382812,  0.500000,
            0.000000,  0.250000,  1.250000,  0.242536,  0.000000, -0.970143,  0.335938,  0.500000,
            -1.000000,  0.250000,  1.000000,  0.242536,  0.000000, -0.970142,  0.574219,  0.500000,
            0.750000, -1.750000,  1.000000, -0.315981,  0.039498, -0.947943,  0.515625,  0.023438,
            0.000000, -1.750000,  1.250000, -0.315981,  0.039498, -0.947943,  0.335938,  0.023438,
            1.000000,  0.250000,  1.000000, -0.315981,  0.039498, -0.947943,  0.574219,  0.500000,
            -0.750000, -1.750000,  1.000000,  0.315981,  0.039498, -0.947943,  0.515625,  0.023438,
            -1.000000,  0.250000,  1.000000,  0.315981,  0.039498, -0.947943,  0.574219,  0.500000,
            0.000000, -1.750000,  1.250000,  0.315981,  0.039498, -0.947943,  0.335938,  0.023438,
            -0.200000,  0.250000,  1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.074219,
            -0.200000,  0.250000,  1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.074219,
            0.600000,  2.250000,  1.350000,  0.000000,  1.000000,  0.000000,  0.816406,  0.121094,
            0.200000,  2.250000,  1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.222656,
            0.200000,  2.250000,  1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.222656,
            0.600000,  2.250000,  1.100000,  0.000000,  1.000000,  0.000000,  0.753906,  0.121094,
            -1.000000,  0.250000,  1.250000, -0.980581,  0.196116,  0.000000,  0.695312,  0.500000,
            -0.600000,  2.250000,  1.350000, -0.980581,  0.196116,  0.000000,  0.695312,  0.976562,
            -0.600000,  2.250000,  1.100000, -0.980581,  0.196116,  0.000000,  0.636719,  0.976562,
            -1.000000,  0.250000,  1.000000, -0.980581,  0.196116,  0.000000,  0.636719,  0.500000,
            -0.200000,  0.250000,  1.200000,  1.000000,  0.000000,  0.000000,  0.753906,  0.500000,
            -0.200000,  2.250000,  1.200000,  1.000000,  0.000000,  0.000000,  0.753906,  0.976562,
            -0.200000,  0.250000,  1.450000,  1.000000,  0.000000,  0.000000,  0.816406,  0.500000,
            -0.200000,  2.250000,  1.450000,  1.000000,  0.000000,  0.000000,  0.816406,  0.976562,
            1.000000,  0.250000,  1.000000,  0.980581,  0.196116,  0.000000,  0.636719,  0.500000,
            0.600000,  2.250000,  1.100000,  0.980581,  0.196116,  0.000000,  0.636719,  0.976562,
            1.000000,  0.250000,  1.250000,  0.980581,  0.196116,  0.000000,  0.695312,  0.500000,
            0.600000,  2.250000,  1.350000,  0.980581,  0.196116,  0.000000,  0.695312,  0.976562,
            0.600000,  2.250000,  1.100000, -0.242536, -0.000000, -0.970142,  0.480469,  0.976562,
            0.200000,  2.250000,  1.200000, -0.242536, -0.000000, -0.970142,  0.382812,  0.976562,
            0.200000,  2.250000,  1.450000, -1.000000,  0.000000,  0.000000,  0.816406,  0.976562,
            0.200000,  0.250000,  1.200000, -1.000000,  0.000000,  0.000000,  0.753906,  0.500000,
            0.200000,  0.250000,  1.450000, -1.000000,  0.000000,  0.000000,  0.816406,  0.500000,
            0.200000,  2.250000,  1.200000, -1.000000,  0.000000,  0.000000,  0.753906,  0.976562,
            -0.600000,  2.250000,  1.100000,  0.242536, -0.000000, -0.970142,  0.480469,  0.976562,
            -0.200000,  2.250000,  1.200000,  0.242536, -0.000000, -0.970142,  0.382812,  0.976562,
            -0.600000,  2.250000,  1.350000, -0.242536,  0.000000,  0.970142,  0.171875,  0.976562,
            -0.200000,  2.250000,  1.450000, -0.242536,  0.000000,  0.970142,  0.078125,  0.976562,
            -0.600000,  2.250000,  1.350000,  0.000000,  1.000000,  0.000000,  0.816406,  0.121094,
            -0.200000,  2.250000,  1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.222656,
            -0.200000,  2.250000,  1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.222656,
            -0.600000,  2.250000,  1.100000,  0.000000,  1.000000,  0.000000,  0.753906,  0.121094,
            0.600000,  2.250000,  1.350000,  0.242536,  0.000000,  0.970142,  0.171875,  0.976562,
            0.200000,  2.250000,  1.450000,  0.242536,  0.000000,  0.970142,  0.078125,  0.976562,
            -1.000000,  0.250000, -1.250000, -0.315981, -0.039498, -0.947943,  0.269531,  0.500000,
            -0.000000, -1.750000, -1.500000, -0.315981, -0.039498, -0.947943,  0.027344,  0.023438,
            -0.750000, -1.750000, -1.250000, -0.315981, -0.039498, -0.947943,  0.207031,  0.023438,
            -0.000000, -1.750000, -1.500000, -0.242536, -0.000000, -0.970143,  0.027344,  0.023438,
            -1.000000,  0.250000, -1.250000, -0.242536, -0.000000, -0.970142,  0.269531,  0.500000,
            -0.200000,  0.250000, -1.450000, -0.242536, -0.000000, -0.970143,  0.078125,  0.500000,
            -0.000000,  0.250000, -1.500000, -0.242536, -0.000000, -0.970143,  0.027344,  0.500000,
            0.750000, -1.750000, -1.250000,  0.315981, -0.039498, -0.947943,  0.207031,  0.023438,
            -0.000000, -1.750000, -1.500000,  0.315981, -0.039498, -0.947943,  0.027344,  0.023438,
            1.000000,  0.250000, -1.250000,  0.315981, -0.039498, -0.947943,  0.269531,  0.500000,
            0.200000,  0.250000, -1.450000,  0.242536, -0.000000, -0.970143,  0.078125,  0.500000,
            -0.000000, -1.750000, -1.500000,  0.242536, -0.000000, -0.970143,  0.027344,  0.023438,
            -0.000000,  0.250000, -1.500000,  0.242536, -0.000000, -0.970143,  0.027344,  0.500000,
            1.000000,  0.250000, -1.250000,  0.242536, -0.000000, -0.970142,  0.269531,  0.500000,
            0.750000, -1.750000, -1.250000,  0.992278, -0.124035,  0.000000,  0.695312,  0.023438,
            1.000000,  0.250000, -1.000000,  0.992278, -0.124035,  0.000000,  0.636719,  0.500000,
            0.750000, -1.750000, -1.000000,  0.992278, -0.124035,  0.000000,  0.636719,  0.023438,
            1.000000,  0.250000, -1.250000,  0.992278, -0.124035,  0.000000,  0.695312,  0.500000,
            -0.750000, -1.750000, -1.000000, -0.992278, -0.124035,  0.000000,  0.636719,  0.023438,
            -1.000000,  0.250000, -1.000000, -0.992278, -0.124035,  0.000000,  0.636719,  0.500000,
            -1.000000,  0.250000, -1.250000, -0.992278, -0.124035,  0.000000,  0.695312,  0.500000,
            -0.750000, -1.750000, -1.250000, -0.992278, -0.124035,  0.000000,  0.695312,  0.023438,
            -0.000000,  0.250000, -1.250000,  0.000000,  1.000000,  0.000000,  0.753906,  0.023438,
            -0.200000,  0.250000, -1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.074219,
            -0.200000,  0.250000, -1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.074219,
            -0.000000,  0.250000, -1.500000,  0.000000,  1.000000,  0.000000,  0.816406,  0.023438,
            -0.000000, -1.750000, -1.250000,  0.000000, -1.000000,  0.000000,  0.753906,  0.269531,
            0.750000, -1.750000, -1.250000,  0.000000, -1.000000,  0.000000,  0.816406,  0.449219,
            0.750000, -1.750000, -1.000000,  0.000000, -1.000000,  0.000000,  0.753906,  0.449219,
            -0.000000, -1.750000, -1.500000,  0.000000, -1.000000,  0.000000,  0.816406,  0.269531,
            -0.750000, -1.750000, -1.250000,  0.000000, -1.000000,  0.000000,  0.816406,  0.449219,
            -0.750000, -1.750000, -1.000000,  0.000000, -1.000000,  0.000000,  0.753906,  0.449219,
            -0.000000, -1.750000, -1.250000,  0.242536,  0.000000,  0.970143,  0.335938,  0.023438,
            -0.000000,  0.250000, -1.250000,  0.242536,  0.000000,  0.970143,  0.335938,  0.500000,
            -0.200000,  0.250000, -1.200000,  0.242536,  0.000000,  0.970143,  0.382812,  0.500000,
            -1.000000,  0.250000, -1.000000,  0.242536,  0.000000,  0.970142,  0.574219,  0.500000,
            -0.000000, -1.750000, -1.250000, -0.242536,  0.000000,  0.970143,  0.335938,  0.023438,
            0.200000,  0.250000, -1.200000, -0.242536,  0.000000,  0.970143,  0.382812,  0.500000,
            -0.000000,  0.250000, -1.250000, -0.242536,  0.000000,  0.970143,  0.335938,  0.500000,
            1.000000,  0.250000, -1.000000, -0.242536,  0.000000,  0.970142,  0.574219,  0.500000,
            -0.750000, -1.750000, -1.000000,  0.315981,  0.039498,  0.947943,  0.515625,  0.023438,
            -0.000000, -1.750000, -1.250000,  0.315981,  0.039498,  0.947943,  0.335938,  0.023438,
            -1.000000,  0.250000, -1.000000,  0.315981,  0.039498,  0.947943,  0.574219,  0.500000,
            0.750000, -1.750000, -1.000000, -0.315981,  0.039498,  0.947943,  0.515625,  0.023438,
            1.000000,  0.250000, -1.000000, -0.315981,  0.039498,  0.947943,  0.574219,  0.500000,
            -0.000000, -1.750000, -1.250000, -0.315981,  0.039498,  0.947943,  0.335938,  0.023438,
            0.200000,  0.250000, -1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.074219,
            0.200000,  0.250000, -1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.074219,
            -0.200000,  2.250000, -1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.222656,
            -0.600000,  2.250000, -1.350000,  0.000000,  1.000000,  0.000000,  0.816406,  0.121094,
            -0.600000,  2.250000, -1.100000,  0.000000,  1.000000,  0.000000,  0.753906,  0.121094,
            -0.200000,  2.250000, -1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.222656,
            0.600000,  2.250000, -1.100000,  0.980581,  0.196116,  0.000000,  0.636719,  0.976562,
            1.000000,  0.250000, -1.000000,  0.980581,  0.196116,  0.000000,  0.636719,  0.500000,
            1.000000,  0.250000, -1.250000,  0.980581,  0.196116,  0.000000,  0.695312,  0.500000,
            0.600000,  2.250000, -1.350000,  0.980581,  0.196116,  0.000000,  0.695312,  0.976562,
            0.200000,  2.250000, -1.200000, -1.000000,  0.000000,  0.000000,  0.753906,  0.976562,
            0.200000,  0.250000, -1.450000, -1.000000,  0.000000,  0.000000,  0.816406,  0.500000,
            0.200000,  0.250000, -1.200000, -1.000000,  0.000000,  0.000000,  0.753906,  0.500000,
            0.200000,  2.250000, -1.450000, -1.000000,  0.000000,  0.000000,  0.816406,  0.976562,
            -0.600000,  2.250000, -1.100000, -0.980581,  0.196116,  0.000000,  0.636719,  0.976562,
            -1.000000,  0.250000, -1.250000, -0.980581,  0.196116,  0.000000,  0.695312,  0.500000,
            -1.000000,  0.250000, -1.000000, -0.980581,  0.196116,  0.000000,  0.636719,  0.500000,
            -0.600000,  2.250000, -1.350000, -0.980581,  0.196116,  0.000000,  0.695312,  0.976562,
            -0.600000,  2.250000, -1.100000,  0.242536, -0.000000,  0.970142,  0.480469,  0.976562,
            -0.200000,  2.250000, -1.200000,  0.242536, -0.000000,  0.970142,  0.382812,  0.976562,
            -0.200000,  0.250000, -1.450000,  1.000000,  0.000000,  0.000000,  0.816406,  0.500000,
            -0.200000,  2.250000, -1.450000,  1.000000,  0.000000,  0.000000,  0.816406,  0.976562,
            -0.200000,  0.250000, -1.200000,  1.000000,  0.000000,  0.000000,  0.753906,  0.500000,
            -0.200000,  2.250000, -1.200000,  1.000000,  0.000000,  0.000000,  0.753906,  0.976562,
            0.200000,  2.250000, -1.200000, -0.242536, -0.000000,  0.970142,  0.382812,  0.976562,
            0.600000,  2.250000, -1.100000, -0.242536, -0.000000,  0.970142,  0.480469,  0.976562,
            0.600000,  2.250000, -1.350000,  0.242536,  0.000000, -0.970142,  0.171875,  0.976562,
            0.200000,  2.250000, -1.450000,  0.242536,  0.000000, -0.970142,  0.078125,  0.976562,
            0.200000,  2.250000, -1.200000,  0.000000,  1.000000,  0.000000,  0.753906,  0.222656,
            0.600000,  2.250000, -1.100000,  0.000000,  1.000000,  0.000000,  0.753906,  0.121094,
            0.600000,  2.250000, -1.350000,  0.000000,  1.000000,  0.000000,  0.816406,  0.121094,
            0.200000,  2.250000, -1.450000,  0.000000,  1.000000,  0.000000,  0.816406,  0.222656,
            -0.600000,  2.250000, -1.350000, -0.242536,  0.000000, -0.970142,  0.171875,  0.976562,
            -0.200000,  2.250000, -1.450000, -0.242536,  0.000000, -0.970142,  0.078125,  0.976562
        };

        float[] verts = new float[vertsRaw.length];
        for(int i = 0; i < verts.length; i++){
            verts[i] = (float)vertsRaw[i];
        }

        short[] indices = {
            0,   1,   2,   3,   4,   5,   6,   4,   3,   7,   8,   9,
            10,  11,  12,  13,  10,  12,  14,  15,  16,  14,  16,  17,
            18,  19,  20,  18,  20,  21,  22,  23,  24,  23,  22,  25,
            26,  27,  28,  27,  26,  29,  30,  28,  27,  30,  27,  31,
            32,  33,  34,  35,  33,  32,  36,  37,  38,  37,  36,  39,
            40,  41,  42,  43,  44,  45,  46,  24,  23,  46,  23,  47,
            48,  49,  50,  49,  48,  51,  52,  53,  54,  52,  54,  55,
            56,  57,  58,  59,  58,  57,  60,  61,  62,  63,  62,  61,
            32,  64,  35,  64,  32,  65,  66,  67,  68,  67,  66,  69,
            39,  70,  37,  37,  70,  71,  72,  12,  73,  12,  72,  13,
            74,  75,  76,  74,  76,  77,   3,  78,  79,  78,   3,   5,
            80,  81,  82,  83,  84,  85,  83,  85,  86,  87,  88,  89,
            90,  91,  92,  93,  91,  90,  94,  95,  96,  95,  94,  97,
            98,  99,  100,  98,  100,  101,  102,  103,  104,  103,  102,  105,
            106,  107,  108,  107,  106,  109,  110,  109,  106,  106,  111,  110,
            112,  113,  114,  115,  112,  114,  116,  117,  118,  117,  116,  119,
            120,  121,  122,  123,  124,  125,  126,  127,  102,  102,  127,  105,
            128,  129,  130,  129,  128,  131,  132,  133,  134,  132,  134,  135,
            136,  137,  138,  137,  136,  139,  140,  141,  142,  141,  140,  143,
            144,  114,  145,  114,  144,  115,  146,  147,  148,  149,  148,  147,
            150,  117,  151,  117,  119,  151,  152,  93,  90,  90,  153,  152,
            154,  155,  156,  154,  156,  157,  85,  158,  159,  158,  85,  84
        };

        model = new Mesh(true, verts.length, indices.length, VertexAttribute.position3, VertexAttribute.normal, VertexAttribute.texCoords);
        model.setVertices(verts);
        model.setIndices(indices);

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
        camera.resize(graphics.getWidth(), graphics.getHeight());

        cam3D.position.set(camera.position.x, camera.position.y, cam3D.far / 2f);
        cam3D.resize(camera.width, camera.height);
        cam3D.update();

        Gl.depthMask(false);
        graphics.clear(Color.black);

        Draw.proj(camera);
        for(int x = -3; x <= 3; x++){
            for(int y = -3; y <= 3; y++){
                Draw.rect(dim2, x * 48f, y * 48f);
            }
        }

        Draw.flush();

        Gl.depthMask(true);
        Gl.clear(Gl.depthBufferBit);

        Gl.enable(Gl.depthTest);

        trns.set(
            Tmp.v31.set(0f, 0f, Mathf.sin(3f, 2f)),
            quat.set(Vec3.Y, Time.time),
            Tmp.v32.set(3f, 3f, 3f)
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
            setUniformf("u_scl", graphics.getWidth() / camera.width, graphics.getHeight() / camera.height);

            // I don't know where this value came from. I literally just did trial-and-error so many times that I came
            // up with this value because some positions misbehaved, or maybe I just suck at math, or both. I don't
            // really care, if it works it works.
            setUniformf("u_zscl", 2.0415f);
        }
    }
}