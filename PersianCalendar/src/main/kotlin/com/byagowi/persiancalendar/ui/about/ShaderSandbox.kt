package com.byagowi.persiancalendar.ui.about

import android.opengl.GLSurfaceView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.byagowi.persiancalendar.databinding.ShaderSandboxBinding
import com.byagowi.persiancalendar.generated.sandboxFragmentShader
import com.byagowi.persiancalendar.ui.map.GLRenderer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showShaderSandboxDialog(activity: FragmentActivity) {
    val frame = object : FrameLayout(activity) {
        // Just to let AlertDialog know there is an editor here so it needs to show the soft keyboard
        override fun onCheckIsTextEditor() = true
    }
    frame.post {
        val binding = ShaderSandboxBinding.inflate(activity.layoutInflater)
        binding.glView.setEGLContextClientVersion(2)
        val renderer = GLRenderer(onError = {
            activity.runOnUiThread { Toast.makeText(activity, it, Toast.LENGTH_LONG).show() }
        })
        binding.glView.setRenderer(renderer)
        binding.glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        binding.inputText.doAfterTextChanged {
            renderer.fragmentShader = binding.inputText.text?.toString() ?: ""
            binding.glView.queueEvent { renderer.compileProgram(); binding.glView.requestRender() }
        }
        binding.inputText.setText(sandboxFragmentShader)
        frame.addView(binding.root)
    }
    val dialog = MaterialAlertDialogBuilder(activity)
        .setView(frame)
        .show()
    // Just close the dialog when activity is paused so we don't get ANR after app switch and etc.
    activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) dialog.cancel()
    })
}
