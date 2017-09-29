package com.example.egaviria.lintkotlin

import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.media.MediaPlayer
import android.widget.ImageButton
import android.hardware.Camera.Parameters
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraAccessException
import android.os.Build

class MainActivity : Activity() {

    var btnSwitch: ImageButton? = null
    private var camera: Camera? = null
    private var isFlashOn: Boolean = false
    private var hasFlash: Boolean = false
    var params: Parameters? = null
    var mp: MediaPlayer? = null

    private var mCameraManager: CameraManager? = null
    private var mCameraId: String? = null


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
 * First check if device is supporting flashlight or not
 */
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);


        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            val alert = AlertDialog.Builder(this@MainActivity)
                    .create()
            alert.setTitle("Error")
            alert.setMessage("Sorry, your device doesn't support flash light!")
            alert.setButton("OK") { dialog, which ->
                // closing the application
                finish()
            }
            alert.show()
            return
        }

        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            mCameraId = mCameraManager!!.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }


        // flash switch button
        btnSwitch = findViewById(R.id.imageButtonLigth)

        /*
         * Switch click event to toggle flash on/off
         */

        btnSwitch!!.setOnClickListener({ view -> changeState() })

    }

    private  fun changeState(){

//        Toast.makeText(this@MainActivity, "cambio" + isFlashOn, Toast.LENGTH_SHORT).show()

        if (isFlashOn) {
            // turn off flash
            turnOffFlash()
        } else {
            // turn on flash
            turnOnFlash()
        }
    }

    // getting camera parameters
    private fun getCamera() {

        if (camera == null) {
            try {
                camera = Camera.open()
                params = camera!!.parameters
            } catch (e: RuntimeException) {
                Log.e("Failed to Open. Error: ", e.message)
            }

        }
    }

    /*
 * Turning On flash
 */
    private fun turnOnFlash() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager!!.setTorchMode(mCameraId, true)
//              playSound()
            }else{
                if (camera == null || params == null) {
                    return
                }
                // play sound
//            playSound()

                params = camera!!.parameters
                params!!.setFlashMode(Parameters.FLASH_MODE_TORCH)
                camera!!.parameters = params
                camera!!.startPreview()

                // changing button/switch image
            }
            isFlashOn = true
            toggleButtonImage()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /*
 * Turning Off flash
 */
    private fun turnOffFlash() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager!!.setTorchMode(mCameraId, false)
//                playOnOffSound()

            }else{
                if (camera == null || params == null) {
                    return
                }
                // play sound
//            playSound()

                params = camera!!.getParameters()
                params!!.setFlashMode(Parameters.FLASH_MODE_OFF)
                camera!!.setParameters(params)
                camera!!.stopPreview()

                // changing button/switch image
            }
            isFlashOn = false
            toggleButtonImage()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /*
 * Toggle switch button images
 * changing image states to on / off
 * */
    private fun toggleButtonImage() {
        if (isFlashOn) {
            btnSwitch!!.setImageResource(R.mipmap.ic_ligth_on)
        } else {
            btnSwitch!!.setImageResource(R.mipmap.ic_ligth_off)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        turnOffFlash()
    }

    override fun onPause() {
        super.onPause()

        // on pause turn off the flash
        turnOffFlash()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()

        // on resume turn on the flash
        if (isFlashOn)
            turnOnFlash()
    }

    override fun onStart() {
        super.onStart()

        // on starting the app get the camera params
        getCamera()
    }

    override fun onStop() {
        super.onStop()

        // on stop release the camera
        if (camera != null) {
            camera!!.release()
            camera = null
        }
    }
}
