package dev.aspirasoft.huntit.model.characters

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.IOException

/**
 * A class that represents a character's sprite.
 *
 * A sprite is a collection of images that are used to represent a character. The sprite is
 * composed of a set of frames, each of which is a single image.
 */
abstract class CharacterSprite internal constructor(
    dir: String,
    faceImage: String,
    fullImage: String,
    walkingAnim: String,
) {
    private val faceImagePath: String
    private val fullImagePath: String
    private val walkingAnimPath: String

    init {
        var dir = dir
        if (!dir.endsWith("/")) dir += "/"
        faceImagePath = dir + faceImage
        fullImagePath = dir + fullImage
        walkingAnimPath = dir + walkingAnim
    }

    val faceImageUri: Uri
        get() {
            val uriString = DIR_ASSETS + DIR_CHARACTERS + faceImagePath
            return Uri.parse(uriString)
        }
    val fullImageUri: Uri
        get() {
            val uriString = DIR_ASSETS + DIR_CHARACTERS + fullImagePath
            return Uri.parse(uriString)
        }
    val walkingAnimUri: Uri
        get() {
            val uriString = DIR_ASSETS + DIR_CHARACTERS + walkingAnimPath
            return Uri.parse(uriString)
        }

    fun getFaceDrawable(context: Context): Drawable? {
        return try {
            Drawable.createFromStream(context.assets.open(DIR_CHARACTERS + faceImagePath), null)
        } catch (e: IOException) {
            null
        }
    }

    fun getFullDrawable(context: Context): Drawable? {
        return try {
            Drawable.createFromStream(context.assets.open(DIR_CHARACTERS + fullImagePath), null)
        } catch (e: IOException) {
            null
        }
    }

    fun getWalkDrawable(context: Context): Drawable? {
        return try {
            Drawable.createFromStream(context.assets.open(DIR_CHARACTERS + walkingAnimPath), null)
        } catch (e: IOException) {
            null
        }
    }

    companion object {
        private const val DIR_ASSETS = "asset:///"
        private const val DIR_CHARACTERS = "characters/"
    }
}