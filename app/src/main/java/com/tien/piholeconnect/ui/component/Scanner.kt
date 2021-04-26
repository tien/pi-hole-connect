package com.tien.piholeconnect.ui.component

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
@Composable
fun Scanner(
    barcodeScanner: BarcodeScanner,
    onBarcodeScanSuccess: (Iterable<Barcode>) -> Unit
) {
    val lifecycleOwner = remember {
        object : LifecycleOwner {
            val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

            init {
                lifecycleRegistry.currentState = Lifecycle.State.STARTED
            }

            override fun getLifecycle() = lifecycleRegistry
        }
    }

    DisposableEffect(Unit) {
        object : DisposableEffectResult {
            override fun dispose() {
                lifecycleOwner.lifecycle.currentState = Lifecycle.State.DESTROYED
            }
        }
    }

    AndroidView(factory = { context ->
        val viewfinder = PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()
                    .also { it.setSurfaceProvider(viewfinder.surfaceProvider) }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            { imageProxy ->
                                imageProxy.image?.let { mediaImage ->
                                    val image = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )

                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            onBarcodeScanSuccess(barcodes)
                                            imageProxy.close()
                                        }
                                }
                            })
                    }

                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                    preview
                )
            },
            ContextCompat.getMainExecutor(context)
        )

        viewfinder
    })
}