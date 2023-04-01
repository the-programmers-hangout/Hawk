package me.ddivad.hawk.dataclasses

import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class FurryNames(val names: MutableList<String> = mutableListOf()): Data() {
    fun getRandomName(): String {
        val firstPart = names.random()
        var secondPart = names.random()
        while (secondPart == firstPart) {
            secondPart = names.random()
        }
        return "$firstPart $secondPart"
    }
}