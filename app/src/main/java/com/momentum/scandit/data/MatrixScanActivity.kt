package com.momentum.scandit.data

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import com.momentum.scandit.R
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.tracking.capture.*
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayStyle
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.source.Camera
import com.scandit.datacapture.core.source.FrameSourceState
import com.scandit.datacapture.core.source.VideoResolution
import com.scandit.datacapture.core.ui.DataCaptureView

class MatrixScanActivity : CameraPermissionActivity(), BarcodeTrackingListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    val SCANDIT_LICENSE_KEY = "AV2iAz79HCmwJ3BYGwopAUoYipFtFY1MynV+5390+VWqHxRQw06d6rh2aiV6bglD8E2HhhNc7FwWb2j9BVkJk9x9z0lOXSksajp78YB0owgeRicXNEHW1K1mjVtIMxg3lB8U5yEPYL0EbbCyQnVyhJdwqmx0VQRnPEhftQZW2C5JadRQ2STw6CYmT7nZBtGl81ewpboa1PpsaZ0UOmdGizd+Qmr7bd2wYjYzhtFObYKyJSI54Fui3ThmqaVFd91W53oB5AJcUH3RSQdGIHmQbkFZ1er6YqVV4WQRGrxnwN2Qa3YF0AcrOP4u9qoTcdi3HRnrxY5sOxgCFP0PSFjLAdxU8BvQV2TbxkV4EtAAXwnbKjETWyRzwbQhJ+OrQY/Rbk3s19tm2xe3cG482GzlZBBNAdSKdge0fxy5P5lfwefIUlWRR1BanQZp1rQTfYmAr0aH0xpkaRPfU+ZaFkwVY/A9Hsxxaa0zxxp6gxFZeDDMVYoHF2AKVQh2YAWlSHkJWVadE0ZkjM9mYSdC0QfqTDRBviNUbwsWD2yIvMsgG21FygfQ/4UajCJDmD2E8O5MmHYU/l416oNXUhbFISm4YzEeZ6/3FP/1Vjbgio71NCEcAG6sxv/9shJ1ZTRZEijU5ZbKBKkChXOIztTERrYmbLLeqMKB1LCbyJS9/kCAzLtWAmWuQhx6JjtwoHBfXadAqNtA5nNNjCVdezZ4Ea959bI1AJkLOCxz3HXAdsZ2Ys5YgxrsGQDSu2x41tsNCD1A0siTsebXXo7sNY1yBO9EZmQNfvvHqyvoaOjwGE8YOzwDPP6LItvqY5VKRuTKOJDGxQLudkRjd5ungQcBNqy13TEttXr9r01KN0zggTlzWmmqVf0xw2P39jvVmAyVG0xVAcZWGlVFZEtl6Vfqyzxix1C9XBhQBOt0Wa8YhK01iFGX2fF8nvvSnNBVOX3GXcdNtmijHm39zMVQtxvVqw/+KgSvYRrg8iPMqcZ0OwpZ74WbVeDwzcdYvOXuGdpFM5cuWm0pwiLTLF9rTvWm8A8ILqmTe6VwnBVShmHBJnW1n0Zv3IzW/8GARSP60cp886sLAvE/gXun7EIxj8hPYAyg4E+7owalbl22i3IIVQdJQEYvXIiQNfU84SKR3nFULhXPuFynrrIC8CMyjg0H+zPB6zoDOVyuGBpIrJ/TUKV66RScOz8TtQD139Psi2BYLkaf4CIp6RWGnt+aGC8/qZVi+g=="
    val REQUEST_CODE_SCAN_RESULTS = 1

    private var camera: Camera? = null
    private var barcodeTracking: BarcodeTracking? = null
    private var dataCaptureContext: DataCaptureContext? = null

    private val scanResults = HashSet<ScanResult>()


    override fun onCameraPermissionGranted() {
        resumeFrameSource()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matrix_scan)

        // Initialize and start the barcode recognition.
        initialize()

        val doneButton: Button = findViewById(R.id.done_button)
        doneButton.setOnClickListener {
            synchronized(scanResults) {

                // Show new screen displaying a list of all barcodes that have been scanned.
                val intent: Intent =
                    ResultsActivity().getIntent(this@MatrixScanActivity, scanResults)!!
                startActivityForResult(intent, REQUEST_CODE_SCAN_RESULTS)
            }
        }
    }

    private fun initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY)
        // Use the recommended camera settings for the BarcodeTracking mode.
        val cameraSettings = BarcodeTracking.createRecommendedCameraSettings()
        // Adjust camera settings - set Full HD resolution.
        cameraSettings.preferredResolution = VideoResolution.FULL_HD
        // Use the default camera and set it as the frame source of the context.
        // The camera is off by default and must be turned on to start streaming frames to the data
        // capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
//        camera = getDefaultCamera.getDefaultCamera(cameraSettings)
        camera = Camera.getDefaultCamera(cameraSettings)
        if (camera != null) {
            dataCaptureContext!!.setFrameSource(camera)
        } else {
            throw IllegalStateException("Sample depends on a camera, which failed to initialize.")
        }
        // Scenario A is used as an example to show how the scenario has to be set to configure
        // barcode tracking properly.
        // Please choose the right scenario depending on your exact use case; you can find more
        // information at https://docs.scandit.com/data-capture-sdk/android/barcode-capture/barcode-tracking-scenarios.html.
        // Feel free to contact support@scandit.com if you have any questions about it.
        val scenario = BarcodeTrackingScenario.A
        // The barcode tracking process is configured through barcode tracking settings
        // which are then applied to the barcode tracking instance that manages barcode tracking.
        val barcodeTrackingSettings = BarcodeTrackingSettings.forScenario(scenario)
        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        val symbologies = java.util.HashSet<Symbology>()
        symbologies.add(Symbology.EAN13_UPCA)
        symbologies.add(Symbology.EAN8)
        symbologies.add(Symbology.UPCE)
        symbologies.add(Symbology.CODE39)
        symbologies.add(Symbology.CODE128)
        barcodeTrackingSettings.enableSymbologies(symbologies)
        // Create barcode tracking and attach to context.
        barcodeTracking = BarcodeTracking.forDataCaptureContext(dataCaptureContext, barcodeTrackingSettings)
        // Register self as a listener to get informed of tracked barcodes.
        barcodeTracking!!.addListener(this)
        // To visualize the on-going barcode tracking process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        val dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext)
        // Add a barcode tracking overlay to the data capture view to render the tracked barcodes on
        // top of the video preview. This is optional, but recommended for better visual feedback.
        BarcodeTrackingBasicOverlay.newInstance(barcodeTracking!!, dataCaptureView, BarcodeTrackingBasicOverlayStyle.FRAME)
        // Add the DataCaptureView to the container.
        val container: FrameLayout = findViewById(R.id.data_capture_view_container)
        container.addView(dataCaptureView)
    }


    private fun pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        barcodeTracking!!.isEnabled = false
        camera!!.switchToDesiredState(FrameSourceState.OFF, null)
    }


    private fun resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeTracking!!.isEnabled = true
        camera!!.switchToDesiredState(FrameSourceState.ON, null)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SCAN_RESULTS && resultCode == ResultsActivity().RESULT_CODE_CLEAN) {
            synchronized(scanResults) { scanResults.clear() }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onObservationStarted(barcodeTracking: BarcodeTracking) {
//        super.onObservationStarted(barcodeTracking)
//    }
//
//    override fun onObservationStopped(barcodeTracking: BarcodeTracking) {
//        super.onObservationStopped(barcodeTracking)
//    }

    override fun onSessionUpdated(mode: BarcodeTracking, session: BarcodeTrackingSession, data: FrameData) {
        synchronized(scanResults) {
            for (trackedBarcode in session.addedTrackedBarcodes) {
                scanResults.add(ScanResult(trackedBarcode.barcode))
            }
        }
        super.onSessionUpdated(mode, session, data)
    }
    override fun onResume() {
        super.onResume()

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission()
    }

    override fun onPause() {
        pauseFrameSource()
        super.onPause()
    }

    override fun onDestroy() {
        dataCaptureContext!!.removeMode(barcodeTracking!!)
        super.onDestroy()
    }
}