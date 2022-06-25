package com.wecancity.robusta_weather_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.wecancity.robusta_weather_app.factory.WeatherInformationProviderFactory
import com.wecancity.robusta_weather_app.models.currentWeather.CurrentWeatherModel
import com.wecancity.robusta_weather_app.repository.WeatherInformationRepository
import com.wecancity.robusta_weather_app.viewModel.WeatherInformationViewModel
import com.wecancity.robusta_weather_app.viewUtils.toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherInformationViewModel
    private var thumbnailPath: Uri? = null
    private var thumbnail: Bitmap? = null
    private var myLongitude: Double? = null
    private var mylatitude: Double? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var currentPhotoPath: String

    companion object {
        private const val REQUEST_CODE_IMAGE_CAPTURE = 200

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // permissions to access the storage
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1)
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermission()
        uploadImageBtn.setOnClickListener { initOpenCamera() }
        shareBtn.setOnClickListener {
            try {
                val bitmap = getScreenShotFromView(frame)
                if (bitmap != null) {
                    saveMediaToStorage(bitmap)
                }
                val uri: Uri = getImageToShare(bitmap!!)!!
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                // adding text to share
                /* intent.putExtra(Intent.EXTRA_TEXT,
                     "${weatherCountryTxt.text}-${weatherTempTxt.text}-${weatherTempTxt.text}-${weatherTempTxt.text}-${weatherWindSpeedTxt.text}-")*/
                intent.type = "image/png"
                startActivity(Intent.createChooser(intent, "Share Via"))
            } catch (e: Throwable) {
                Log.d("faceLogin", "onCreate: $e")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ) {
                    //  toast("PERMISSION_GRANTED")
                    getLocations()
                } else {
                    toast("PERMISSION_DENIED")

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_CAPTURE && data != null) {
            when (requestCode) {
                REQUEST_CODE_IMAGE_CAPTURE -> {
                    thumbnail = data.extras?.get("data") as Bitmap
                    thumbnailPath = getImageUri(applicationContext, thumbnail!!)
                    uploadImagePreview.setImageBitmap(thumbnail)
                    getWeatherData()

                }
            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    private fun initOpenCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.i("initOpenCamera", "initOpenCamera: $e")
        }
    }
    private fun getImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "shared_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file)
        } catch (e: Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_LONG).show()
        }
        return uri
    }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
        } else {
            getLocations()
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLocations() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it == null) {
                toast("Sorry We Cant Get Location")
            } else
                it.apply {
                    mylatitude = it.latitude
                    myLongitude = it.longitude
                }
        }
    }
    private fun getWeatherData() {
        val weatherDataRepository = WeatherInformationRepository()
        val weatherDataProviderFactory = WeatherInformationProviderFactory(weatherDataRepository)
        viewModel = ViewModelProvider(this,
            weatherDataProviderFactory)[WeatherInformationViewModel::class.java]
        viewModel.getWeatherInformation(mylatitude!!, myLongitude!!)
        viewModel.weatherInformationLiveData.observe(this, Observer { response ->
            if (response.cod == 200) {
                Log.d("getAllCategories", "getAllCategories: ${response.name}")
                toast(response.name)
                onGetWeatherSuccess(response)
            } else {
                toast("Some Thing Is Going To Be Error")
            }
        })
    }
    @SuppressLint("SetTextI18n")
    private fun onGetWeatherSuccess(weatherDataModel: CurrentWeatherModel) {
        placeDetailsContainer.visibility = View.VISIBLE
        weatherCountryTxt.text = "${weatherDataModel.sys.country}-${weatherDataModel.name}/"
        weatherConditionTxt.text =
            "${weatherDataModel.weather[0].main}-${weatherDataModel.weather[0].description}/"
        weatherTempTxt.text = "${(weatherDataModel.main.temp - 273.15).toInt()}ْ /"
        weatherWindSpeedTxt.text = "${weatherDataModel.wind.speed}-${weatherDataModel.wind.deg}"
    }
    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return screenshot
    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.i("dispatchTake", "dispatchTakePictureIntent: $ex")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
                }
            }
        }
    }
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}