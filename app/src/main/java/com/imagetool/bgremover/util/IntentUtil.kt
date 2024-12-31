package com.imagetool.bgremover.util

import android.app.Activity
import android.content.Context
import android.content.Intent

enum class IntentKeys {
    PickedImageUri
}

object IntentUtil {

    fun intent(context: Context, dest: Class<*>,flags:List<Int>){
        val intent = Intent(context,dest).apply {
             flags.forEach {
                 addFlags(it)
             }
        }

        context.startActivity(intent)
    }

    fun <T> intentWithArgs(context: Context, dest: Class<*>, argKey: IntentKeys, arg: T,flags: List<Int>) {
        val intent = Intent(context, dest).apply {
            flags.forEach {
                addFlags(it)
            }
            when (arg) {
                is String -> putExtra(argKey.name, arg)
                else -> {
                    throw IllegalArgumentException("Not supported type for Intent $arg")
                }
            }
        }

        context.startActivity(intent)
    }

    inline fun <reified T : Any> getArgs(activity: Activity, argKey: IntentKeys): T {
        return when {
            T::class.java.isAssignableFrom(String::class.java) -> activity.intent.getStringExtra(
                argKey.name
            ) as T

            else -> {
                throw IllegalArgumentException("No data found of key ${argKey.name}")
            }
        }
    }
}