package com.vincentagung.ugd89_a_10683_project1

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    private var currentCameraId: Int = Camera.CameraInfo.CAMERA_FACING_BACK
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager


    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if(event.values[0] == 0f){
                if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                if (mCameraView != null) {
                    mCamera?.stopPreview();
                }
                //NB: if you don't release the current camera before switching, you app will crash
                mCamera?.release();

                try {
                    mCamera = Camera.open(currentCameraId)
                } catch (e: Exception) {
                    Log.d("Error", "Failed to get Camera" + e.message)
                }

                if(mCamera != null){
                    mCameraView = CameraView(applicationContext, mCamera!!)
                    val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
                    camera_view.addView(mCameraView)
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try{
            mCamera = Camera.open()
        }catch (e: Exception){
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if(mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if(proximitySensor == null){
            Toast.makeText(this,"No Proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener{ view: View? -> System.exit(0) }
    }
}