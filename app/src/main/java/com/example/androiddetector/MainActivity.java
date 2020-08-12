package com.example.androiddetector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.opencv.dnn.Dnn;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    boolean startDetect = false;
    boolean firstTimeYolo = false;
    Net yoloV3;
    //记录帧数
    int counter = 0;

    String TAG = "TEST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        //查看一切是否正常
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }

            }
        };

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startDetect ==false){
                    startDetect = true;
                    //在开始的时候需要加载一下模型,only once
                    if(firstTimeYolo == false){
                        firstTimeYolo = true;//only once
                        //如果是从手机外部存储空间中读取数据，需要获得externalstorage的permission
                        Log.i(TAG,"load model");
                        String ONNXmodel = Environment.getExternalStorageDirectory()+"/dnn/yolov3-myyolov3_99_0.96_warehouse.onnx";
                        yoloV3 = Dnn.readNet(ONNXmodel);
                    }
                }
                else{
                    startDetect = false;
                }
            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //点亮屏幕时
    }

    @Override
    public void onCameraViewStopped() {
        //黑屏时
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //获取每一帧时
        Mat frame = inputFrame.rgba();

        if(startDetect ==true){
            //边缘检测代码
//            Imgproc.cvtColor(frame,frame,Imgproc.COLOR_RGB2GRAY);
//            Imgproc.Canny(frame,frame,100,80);

            //在屏幕中绘制一个框
//            int centerX = frame.cols()/2;
//            int centerY = frame.rows()/2;
//
//            int width = frame.cols()/4;
//            int height = frame.rows()/4;
//
//            int left = centerX - width/2;
//            int top = centerY - height/2;
//
//            int right = centerX + width/2;
//            int bottom = centerY + height/2;
//
//            Point leftTop = null;
//            Point rightBottom = null;
//
//            switch(counter%3){
//                case 0:
//                    leftTop = new Point(left,top);
//                    rightBottom = new Point(right,bottom);
//                    Imgproc.putText(frame,"First Anchor",leftTop,Core.FONT_HERSHEY_COMPLEX,1,new Scalar(255,255,0));
//                    Imgproc.rectangle(frame,leftTop,rightBottom,new Scalar(255,0,0),2);
//                case 1:
//                    leftTop = new Point(left-frame.cols()/4,top-frame.rows()/4);
//                    rightBottom = new Point(right-frame.cols()/4,bottom-frame.rows()/4);
//                    Imgproc.putText(frame,"First Anchor",leftTop,Core.FONT_HERSHEY_COMPLEX,1,new Scalar(255,255,0));
//                    Imgproc.rectangle(frame,leftTop,rightBottom,new Scalar(255,0,0),2);
//                case 2:
//                    leftTop = new Point(left+frame.cols()/4,top+frame.rows()/4);
//                    rightBottom = new Point(right+frame.cols()/4,bottom+frame.rows()/4);
//                    Imgproc.putText(frame,"First Anchor",leftTop,Core.FONT_HERSHEY_COMPLEX,1,new Scalar(255,255,0));
//                    Imgproc.rectangle(frame,leftTop,rightBottom,new Scalar(255,0,0),2);
//            }


            Log.i(TAG,"start to work");
            //输入数据预处理
            Imgproc.cvtColor(frame,frame,Imgproc.COLOR_RGBA2RGB);
            Mat imageBlob = Dnn.blobFromImage(frame,0.00392,new Size(416,416),new Scalar(0,0,0),false,false);
            //获取输入
            yoloV3.setInput(imageBlob);
            //执行模型
            yoloV3.forward();

        }
        counter++;
        return frame;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            //出现问题时
            Toast.makeText(getApplicationContext(),"Something Wrong!!",Toast.LENGTH_LONG);
        }
        else{
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停时关闭
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}