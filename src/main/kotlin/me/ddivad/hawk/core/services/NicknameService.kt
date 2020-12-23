package me.ddivad.hawk.core.services

import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.Member
import me.ddivad.hawk.core.dataclasses.Configuration
import me.ddivad.hawk.core.dataclasses.GuildConfiguration
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service

@Service
class NicknameService(private val discord: Discord) {
    val changedNicknameMap: MutableMap<String, String> = mutableMapOf()

    suspend fun renameMember(member: Member) {
        member.edit { nickname = "test" }
    }

    fun applyNickPrefix(name: String, symbol: String, stripString: String, mode: String): String {
        val newName = name.replace(stripString, "").replace("\\s+|", "")
        val nickWithPrefix = if (mode.toLowerCase() == "prefix") {
            "$symbol $newName"
        } else {
            "$newName $symbol"
        }

        val sizedNick = if (nickWithPrefix.length > 32) {
            if (mode.toLowerCase() == "prefix") {
                nickWithPrefix.substring(0, 31)
            } else {
                nickWithPrefix.substring(0, 31 - symbol.length) + symbol
            }
        } else {
            nickWithPrefix
        }

        val discordNick = sizedNick.replace(" ", "").replace(stripString, "")

        return if (discordNick.isBlank()) {
            "$symbol Blanky blankerson"
        } else {
            sizedNick
        }
    }

    fun removeNickPrefix(name: String, prefix: String): String {
        val strippedName = name.replace(prefix, "")

        return if (strippedName.isBlank() || strippedName == "") {
            "Chunck Testa"
        } else {
            name.replace(prefix, "")
        }
    }

    private fun nicknameIsValid(name: String, nameWithoutPrefix: String, configuration: GuildConfiguration): Boolean {
        return (configuration.mode == "prefix" &&
                name.startsWith(configuration.nickSymbol)) || name.endsWith(configuration.nickSymbol.replace(" ", "")) &&
                !(nameWithoutPrefix.contains(configuration.stripString))
    }
}