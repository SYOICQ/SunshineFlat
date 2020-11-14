package CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Util.GL2Utils;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.frustumM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.translateM;

public class GLView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private float mtouchX;
    private float mtouchY;
    private Bitmap currentMap;

    private Context context;
    //渲染管线程序程序id
    private int mProgram;
    //顶点位置属性引用id
    private int mAPostionHandler;
    //总变换矩阵应用id
    private int mUProjectMatrixHandler;
    //顶点纹理坐标属性引用id
    private int mATextureCoordHandler;
    //纹理id
    private int textureID;
    //投影矩阵
    private final float[]projectMatrix = new float[16];
    //当前变换矩阵
    public final float mCurrMatrix[] = new float[16];
    //总变换据矩阵
    public final float mMVPMatrix[] = new float[16];
    //顶点数量
    private int mSize;
    //顶点坐标数据缓冲
    private FloatBuffer vertexBuff;
    //顶点纹理坐标数据缓冲
    private FloatBuffer textureBuff;
    //x轴角度
    public float xAngle;
    //y轴角度
    public float yAngle;
    //z轴角度
    public float zAngle;

    private boolean flag = true;

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //设置版本
        setEGLContextClientVersion(2);
        setRenderer(this);
        //自动渲染
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        initVertexData();
    }

    //初始化顶点数据
    private void initVertexData() {
        //球体每一圈顶点的数量(圆的周长)
        int perVertex= 256;
        //求出球的半径
        double perRadius = 2*Math.PI/(float)perVertex;
        //纹理列数
        double perW = 1/(float)perVertex;
        //纹理行数
        double perH = 1/(float)perVertex;
        //存储球体顶点的list
        ArrayList<Float> vertexList = new ArrayList<>();
        //存储纹理数据的list
        ArrayList<Float> textureList = new ArrayList<>();
        for(int a=0;a<perVertex;a++){//纵向
            for(int b=0;b<perVertex;b++){//横向
                //生成纹理坐标点的一个矩形 两个三角形 六个点 12个纹理坐标
                float w1 = (float) (a*perH);
                float h1 = (float) (b*perW);

                float w2 = (float) ((a+1)*perW);
                float h2 = (float) (b*perW);

                float w3 = (float) ((a+1)*perW);
                float h3 = (float) ((b+1)*perW);

                float w4 = (float) (a*perW);
                float h4 = (float) ((b+1)*perW);

                //按顺序加入纹理数据
                textureList.add(h1);
                textureList.add(w1);
                textureList.add(h2);
                textureList.add(w2);
                textureList.add(h3);
                textureList.add(w3);

                textureList.add(h3);
                textureList.add(w3);
                textureList.add(h4);
                textureList.add(w4);
                textureList.add(h1);
                textureList.add(w1);

                //球体顶点坐标 每行列一个矩形 2个三角形 6个顶点 一共18个坐标(3D坐标 x,y,z)
                float x1 = (float) ((Math.sin(a*perRadius/2))*Math.cos(b*perRadius));
                float z1 = (float) ((Math.sin(a*perRadius/2))*Math.sin(b*perRadius));
                float y1 = (float) Math.cos(a*perRadius/2);

                float x2 = (float) ((Math.sin((a+1)*perRadius/2))*Math.cos(b*perRadius));
                float z2 = (float) ((Math.sin((a+1)*perRadius/2))*Math.sin(b*perRadius));
                float y2 = (float) Math.cos((a+1)*perRadius/2);

                float x3 = (float) ((Math.sin((a+1)*perRadius/2))*Math.cos((b+1)*perRadius));
                float z3 = (float) ((Math.sin((a+1)*perRadius/2))*Math.sin((b+1)*perRadius));
                float y3 = (float) Math.cos((a+1)*perRadius/2);

                float x4 = (float) ((Math.sin(a*perRadius/2))*Math.cos((b+1)*perRadius));
                float z4 = (float) ((Math.sin(a*perRadius/2))*Math.sin((b+1)*perRadius));
                float y4 = (float) Math.cos(a*perRadius/2);

                vertexList.add(x1);
                vertexList.add(y1);
                vertexList.add(z1);
                vertexList.add(x2);
                vertexList.add(y2);
                vertexList.add(z2);
                vertexList.add(x3);
                vertexList.add(y3);
                vertexList.add(z3);

                vertexList.add(x3);
                vertexList.add(y3);
                vertexList.add(z3);
                vertexList.add(x4);
                vertexList.add(y4);
                vertexList.add(z4);
                vertexList.add(x1);
                vertexList.add(y1);
                vertexList.add(z1);
            }
        }
        //顶点的个数
        mSize = vertexList.size() / 3;
        //纹理数据缓冲
        float texture[] = new float[mSize*2];
        for(int i=0;i<texture.length;i++){
            texture[i]=textureList.get(i);
        }
        //纹理坐标数组缓冲区
        textureBuff = ByteBuffer.allocateDirect(texture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuff.put(texture);
        textureBuff.position(0);
        //球体顶点坐标缓冲
        float vertex[] = new float[mSize*3];
        for(int i=0;i<vertex.length;i++){
            vertex[i] = vertexList.get(i);
        }
        //球体顶点坐标数组缓冲区
        vertexBuff = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuff.put(vertex);
        vertexBuff.position(0);


    }

    /**
     * 获取物体的总变换矩阵
     * @return
     */
    public float[] getfinalMVPMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, projectMatrix, 0, mCurrMatrix, 0);
        Matrix.setIdentityM(mCurrMatrix, 0);
        return mMVPMatrix;
    }

    /**
     * 当创建 GLSurfaceView时,系统调用这个方法.
     * 使用这个方法去执行只需要发生一次的动作,例如
     * 设置OpenGL环境参数或者初始化OpenGL graphic 对象.
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    /**
     * 当 GLSurfaceView  几何学发生改变时系统调用这个方法.
     * 包括 GLSurfaceView 的大小发生改变或者
     * 横竖屏发生改变.使用这个方法去响应GLSurfaceView
     * 容器的改变.
     * @param gl10
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //视窗大小设置
        glViewport(0,0,width,height);
        //将超出屏幕的范围裁剪掉 不显示
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //glsurfaceview宽高比
        float ratio = width/(float)height;
        /**
         * 这个方法是根据六个剪辑平面定义一个投影矩阵
         * 第一个参数是持有输出透视矩阵的浮点数组
         * 第二个参数是写入透视矩阵数据的浮点数组m的偏移量
         * 剩下的参数以此为剪辑平面位置
         * 左 右 底 顶 近 远
         */
        frustumM(projectMatrix,0,-ratio,ratio,-1,1,1,20);
        //初始化矩阵为恒等矩阵
        Matrix.setIdentityM(mCurrMatrix,0);
        Matrix.setIdentityM(mMVPMatrix,0);
        /**
         * 物体平移
         *
         * 第一个是将要变换的矩阵
         * 第二个是矩阵起始索引
         * 剩下的是各个坐标的转换因子 x,y,z
         *
         */
        translateM(projectMatrix,0,0,0,-2);
        /**
         * 物体按坐标缩放比例缩放
         */
        scaleM(projectMatrix,0,4,4,4);
        //获取渲染管线程序
        mProgram = GL2Utils.getProgram(context);
        glUseProgram(mProgram);
        //获取顶点位置属性id
        mAPostionHandler = glGetAttribLocation(mProgram,"aPosition");
        //获取总变换矩阵id
        mUProjectMatrixHandler = glGetUniformLocation(mProgram,"uProjectMatrix");
        //获取纹理顶点坐标属性id
        mATextureCoordHandler = glGetAttribLocation(mProgram,"aTextureCoord");
        System.out.println("mAPositionHandler:" + mAPostionHandler);
        System.out.println("mUProjectMatrixHandler:" + mUProjectMatrixHandler);
        System.out.println("mATextureCoordHandler:" + mATextureCoordHandler);




        //为画笔指定顶点位置数据glActiveTexture
        glVertexAttribPointer(mAPostionHandler,3,GL_FLOAT,false,0,vertexBuff);
        //为画笔指定顶点纹理数据
        glVertexAttribPointer(mATextureCoordHandler,2,GL_FLOAT,false,0,textureBuff);
        //允许顶点位置数据数组
        glEnableVertexAttribArray(mAPostionHandler);
        //允许顶点纹理数据数组
        glEnableVertexAttribArray(mATextureCoordHandler);
    }

    /**
     * 当系统每一次重画 GLSurfaceView 时调用.使用这个方法去
     * 作为主要的绘制和重新绘制graphic  对象的执行点.
     * @param gl
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        //设置摄像机方向矩阵
        rotateM(mCurrMatrix,0,-xAngle,1,0,0);
        rotateM(mCurrMatrix,0,-yAngle,0,1,0);
        rotateM(mCurrMatrix,0,-zAngle,0,0,1);
        //设置屏幕背景色
        glClearColor(1,1,1,1);
        //清除颜色缓冲和深度缓冲
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        //开启当前活跃的纹理
        glActiveTexture(GL_TEXTURE0);
        if(flag) {
            //初始化纹理
            textureID = GL2Utils.initTexture(context, currentMap);
            System.out.println("textureID:" + textureID);
            flag = false;
        }
        //绑定纹理
        glBindTexture(GL_TEXTURE_2D,textureID);
        //转换向量矩阵
        glUniformMatrix4fv(mUProjectMatrixHandler,1,false,getfinalMVPMatrix(),0);
        //绘制图形以及纹理(GL_TRIANGLES:3点一个坐标)
        glDrawArrays(GL_TRIANGLES,0,mSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取点击坐标的位置
        float y = event.getY();
        float x = event.getX();

        switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dy =y-mtouchY;
                float dx =x-mtouchX;
                //改变摄像机视角
                xAngle+=dy*0.3f;
                yAngle+=dx*0.3f;
        }
        mtouchX = x;
        mtouchY = y;
        return true;
    }

    public void setImage(Context context,Bitmap bitmap) {
        //初始化纹理
        currentMap = bitmap;
        //textureID = GL2Utils.initTexture(context,bitmap);

        setRenderMode(RENDERMODE_WHEN_DIRTY);
        flag = true;
        requestRender();
        //自动渲染
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

}
