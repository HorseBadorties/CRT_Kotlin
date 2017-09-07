package de.toto.crt.game.gui.javafx

import java.util.prefs.Preferences

object Prefs {

    private val store = Preferences.userNodeForPackage(App::class.java)

    fun getBoolean(key: String, default: Boolean = true) : Boolean = store.getBoolean(key, default)
    fun set(key: String, value: Boolean) = store.putBoolean(key, value)
    fun getString(key: String, default: String? = null) : String? = store.get(key, default)
    fun set(key: String, value: String) = store.put(key, value)
    fun getInt(key: String, default: Int = 0) : Int = store.getInt(key, default)
    fun set(key: String, value: Int) = store.putInt(key, value)
    fun getDouble(key: String, default: Double = 0.0) : Double = store.getDouble(key, default)
    fun set(key: String, value: Double) = store.putDouble(key, value)

    const val WHITE_PERSPECTIVE = "WHITE_PERSPECTIVE"
    const val SHOW_GRAPHICS_COMMENTS = "SHOW_GRAPHICS_COMMENTS"
    const val SHOW_PIECES = "SHOW_PIECES"
    const val SHOW_BOARD = "SHOW_BOARD"
    const val SHOW_COORDINATES = "SHOW_COORDINATES"
    const val FRAME_WIDTH = "FRAME_WIDTH"
    const val FRAME_HEIGHT = "FRAME_HEIGHT"
}
