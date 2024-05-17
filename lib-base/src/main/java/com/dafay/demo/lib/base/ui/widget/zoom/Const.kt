package com.dafay.demo.lib.base.ui.widget.zoom


val DEFAULT_MAX_ZOOM = 3f
val DEFAULT_MID_ZOOM = 1.75f
val DEFAULT_MIN_ZOOM = 1f
val DEFAULT_ANIM_DURATION = 400L

enum class Edge(val value: Int){
    EDGE_NONE(-1),
    EDGE_LEFT(0),
    EDGE_RIGHT(1),
    EDGE_BOTH(2)
}