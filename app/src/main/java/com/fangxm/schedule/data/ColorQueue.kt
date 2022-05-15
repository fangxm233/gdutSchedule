package com.fangxm.schedule.data

class ColorQueue {
    val colors = arrayOf(
        "#86aced", "#e287af", "#6da9de",
        "#b39cd0", "#ff9496", "#ffae7f",
        "#477ec0", "#007371", "#7daeb2",
        "#84BE24", "#b2907d", "#CA54BA",
        "#88b27d", "#CAB354", "#ee8f5f")

    var index = 0;

    fun next(): String {
        if (index == colors.size) index = 0
        return colors[index++]
    }

    fun current(): String {
        return colors[index]
    }

    companion object {
        const val examColor = "#000000"
    }
}