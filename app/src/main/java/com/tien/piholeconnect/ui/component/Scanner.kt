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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
@Composable
fun Scanner(
    modifier: Modifier = Modifier,
    barcodeScanner: BarcodeScanner,
    onBarcodeScanSuccess: (Iterable<Barcode>) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var cameraProvider: ProcessCameraProvider? = null

    // TODO: Investigate further
    // from memory, LocalLifecycleOwner used to close the camera
    // when composition exit from view, this no longer seems to be the case
    // hence we have to close camera manually
    DisposableEffect(Unit) { onDispose { cameraProvider?.unbindAll() } }

    AndroidView(
        factory = { context ->
            val viewfinder =
                PreviewView(context).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener(
                {
                    cameraProvider = cameraProviderFuture.get()

                    val preview =
                        Preview.Builder().build().also {
                            it.setSurfaceProvider(viewfinder.surfaceProvider)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    val imageAnalysis =
                        ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy
                                    ->
                                    imageProxy.image?.let { mediaImage ->
                                        val image =
                                            InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees,
                                            )

                                        barcodeScanner.process(image).addOnSuccessListener {
                                            barcodes ->
                                            onBarcodeScanSuccess(barcodes)
                                            imageProxy.close()
                                        }
                                    }
                                }
                            }

                    cameraProvider?.unbindAll()

                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageAnalysis,
                        preview,
                    )
                },
                ContextCompat.getMainExecutor(context),
            )

            viewfinder
        },
        modifier = modifier,
    )
}
