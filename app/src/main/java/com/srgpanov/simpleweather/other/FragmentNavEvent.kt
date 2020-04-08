package com.srgpanov.simpleweather.other

import android.os.Bundle
import androidx.fragment.app.Fragment

class FragmentNavEvent(val clazz: Class<*>, val bundle: Bundle?=null, val tag: String = clazz.simpleName) {

    fun buildFragment(): Fragment {
        return (clazz.getConstructor().newInstance() as Fragment).apply {
            bundle?.let {
                this.arguments = bundle
            }
        }
    }
}