package com.example.orderfood.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

@Suppress("DEPRECATION")
abstract class BaseActivity<VB : ViewBinding>(val bindingFactory: (LayoutInflater) -> VB) :
    AppCompatActivity() {

    protected val binding: VB by lazy { bindingFactory(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        hideFullNavigation()

        initView()
        bindView()
    }


    open fun initView() {

    }

    open fun bindView() {

    }


    private fun hideFullNavigation() {
        try {
            val flags =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = flags
            val decorView = window.decorView
            decorView.setOnSystemUiVisibilityChangeListener { visibility: Int ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    decorView.systemUiVisibility = flags
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}