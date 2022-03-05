package dev.aspirasoft.huntrek.utils

import dev.aspirasoft.huntrek.data.repo.TreasureRepository
import dev.aspirasoft.huntrek.data.source.DataSource
import dev.aspirasoft.huntrek.model.collectibles.TreasureChest
import java.util.*
import kotlin.math.sqrt

/**
 * Created by saifkhichi96 on 23/12/2017.
 */
object SpawnUtil {

    private const val MAX_CHESTS = 50

    fun spawnTreasureChests() {
        for (i in 0 until MAX_CHESTS) {
            val chest = TreasureChest(i)
            chest.value = 100 + Random().nextInt(400)
            var latitude: Double
            var longitude: Double
            val r1 = Math.random()
            val r2 = Math.random()

            if (Random().nextInt(2) == 0) { // P = (1 - sqrt(r1)) * A + (sqrt(r1) * (1 - r2)) * B + (sqrt(r1) * r2) * C
                latitude =
                    (1 - sqrt(r1)) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_A.x + sqrt(r1) * (1 - r2) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_B.x + sqrt(
                        r1) * r2 * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_C.x
                longitude =
                    (1 - sqrt(r1)) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_A.y + sqrt(r1) * (1 - r2) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_B.y + sqrt(
                        r1) * r2 * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_C.y
            } else {
                latitude =
                    (1 - sqrt(r1)) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_A.x + sqrt(r1) * (1 - r2) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_D.x + sqrt(
                        r1) * r2 * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_C.x
                longitude =
                    (1 - sqrt(r1)) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_A.y + sqrt(r1) * (1 - r2) * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_D.y + sqrt(
                        r1) * r2 * dev.aspirasoft.huntrek.data.HuntBoundary.POINT_C.y
            }
            chest.latitude = latitude
            chest.longitude = longitude

            TreasureRepository.add(chest)
            saveSpawnTime()
        }
    }

    fun checkDaySinceLastSpawn(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val lastSpawnTime = DataSource.get("lastSpawnTime", Long::class.java, 0L)
        return lastSpawnTime == 0L || currentTimeMillis - lastSpawnTime!! >= 24 * 60 * 60 * 1000
    }

    private fun saveSpawnTime() {
        DataSource.put("lastSpawnTime", System.currentTimeMillis())
    }

}