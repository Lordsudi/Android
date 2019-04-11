package co.asap.solarsystem

import android.content.Context
import android.view.MotionEvent
import android.widget.TextView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable

class Planet(
    private val context: Context,
    private val planetName: String,
    private val planetScale: Float,
    private val planetRenderable: ModelRenderable,
    private val solarSettings: SolarSettings
) : Node(), Node.OnTapListener {

    private var infoCard: Node? = null
    private var planetVisual: RotatingNode? = null

    init {
        setOnTapListener(this)
    }

    override fun onActivate() {

        if (scene == null) {
            throw IllegalStateException("Scene is null!")
        }

        if (infoCard == null) {
            infoCard = Node()
            infoCard!!.setParent(this)
            infoCard!!.isEnabled = false
            infoCard!!.localPosition = Vector3(0.0f, planetScale * INFO_CARD_Y_POS_COEFF, 0.0f)

            ViewRenderable.builder()
                .setView(context, R.layout.planet_card_view)
                .build()
                .thenAccept { renderable ->
                    infoCard!!.renderable = renderable
                    val textView = renderable.view as TextView
                    textView.text = planetName
                }
                .exceptionally { throwable -> throw AssertionError("Could not load plane card view.", throwable) }
        }

        if (planetVisual == null) {
            planetVisual = RotatingNode(solarSettings, false)
            planetVisual!!.setParent(this)
            planetVisual!!.renderable = planetRenderable
            planetVisual!!.localScale = Vector3(planetScale, planetScale, planetScale)
        }
    }

    override fun onTap(hitTestResult: HitTestResult, motionEvent: MotionEvent) {
        if (infoCard == null) {
            return
        }

        infoCard!!.isEnabled = !infoCard!!.isEnabled
    }

    override fun onUpdate(frameTime: FrameTime?) {
        if (infoCard == null) {
            return
        }





        if (scene == null) {
            return
        }
        val cameraPosition = scene.camera.worldPosition
        val cardPosition = infoCard!!.worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        infoCard!!.worldRotation = lookRotation
    }

    companion object {

        private val INFO_CARD_Y_POS_COEFF = 0.55f
    }
}
