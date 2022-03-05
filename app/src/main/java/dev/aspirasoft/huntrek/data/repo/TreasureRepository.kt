package dev.aspirasoft.huntrek.data.repo

import dev.aspirasoft.huntrek.data.source.DataSource
import dev.aspirasoft.huntrek.model.collectibles.TreasureChest

object TreasureRepository {

    private val dataSource: DataSource = DataSource

    private var MAX_CHEST = dataSource.get("MAX_CHEST", Int::class.java, 0)
        set(value) {
            field = value
            dataSource.put("MAX_CHEST", value)
        }

    val all: List<TreasureChest>
        get() {
            val chests: MutableList<TreasureChest> = ArrayList()
            MAX_CHEST?.let { max ->
                for (i in 0 until max) {
                    dataSource.get("Chest #$i", TreasureChest::class.java)?.let {
                        chests.add(it)
                    }
                }
            }
            return chests
        }

    val size: Int
        get() = all.size

    fun add(chest: TreasureChest) {
        dataSource.put("Chest #${chest.id}", chest)
        MAX_CHEST?.let {
            if (chest.id + 1 > it) {
                MAX_CHEST = chest.id + 1
            }
        }
    }

    fun get(id: Int): TreasureChest? {
        return dataSource.get("Chest #$id", TreasureChest::class.java)
    }

    fun pop(id: Int): TreasureChest? {
        val chest = get(id)
        if (chest != null) {
            remove(id)
        }
        return chest
    }

    private fun remove(id: Int) {
        dataSource.remove("Chest #$id")
    }

}