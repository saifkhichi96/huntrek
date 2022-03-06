package dev.aspirasoft.huntit.model.collectibles

import java.io.Serializable

/**
 * Created by saifkhichi96 on 23/12/2017.
 */
class TreasureChest(val id: Int) : Serializable {

    var longitude = 0.0
    var latitude = 0.0
    var value = 0

    companion object {
        const val RANGE = 2500f
    }

}