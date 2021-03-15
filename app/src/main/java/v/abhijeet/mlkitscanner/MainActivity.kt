package v.abhijeet.mlkitscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.view.CameraView



class MainActivity : AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var barcodeScanner: BarcodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeBarcodeScanner()

        initializeFotoapparat()

        initilizeCam()





    }

    private fun initilizeCam() {


        if (isCameraPermissionGranted()){
            takeImage()
        }

        else {
            requestCameraPermission()
        }


        if (!isCameraPermissionGranted()) requestCameraPermission()


    }


    private fun takeImage() {
        fotoapparat.start()
        fotoapparat.takePicture().toBitmap().whenAvailable {
            scanImageForBarcode(it!!)

        }
    }

    private fun scanImageForBarcode(it: BitmapPhoto) {
        val inputImage = InputImage.fromBitmap(it.bitmap, it.rotationDegrees)
        val task = barcodeScanner.process(inputImage)
        val textView = findViewById<TextView>(R.id.textView1)
        task.addOnSuccessListener { barCodesList ->
            for (barcodeObject in barCodesList) {
                val barcodeValue = barcodeObject.rawValue
                textView.text = barcodeValue
                Log.d("Barcode", "The code %s".format(barcodeValue))
                Toast.makeText(baseContext, " Sucessfull",
                        Toast.LENGTH_LONG).show()



            }
        }
        task.addOnFailureListener {


            Log.d("ERROR", "An Exception occurred", it)

            Toast.makeText(baseContext, " Failed",
                    Toast.LENGTH_LONG).show()
        }

        takeImage()



    }



    private fun initializeFotoapparat() {
        val cameraView = findViewById<CameraView>(R.id.cameraView1)
        fotoapparat = Fotoapparat.with(this)
            .into(cameraView)
            .previewScaleType(ScaleType.CenterCrop)
            .build()


    }

    private fun initializeBarcodeScanner() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_AZTEC
            )
            .build()

        barcodeScanner = BarcodeScanning.getClient(options)
    }


    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }




    private fun isCameraPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            1144
        )
    }
}