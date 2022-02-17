package me.ddivad.hawk.services

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.jakejmattson.discordkt.annotations.Service

@Service
class NicknameService(private val configuration: Configuration, private val loggingService: LoggingService) {
    suspend fun setOrRemovePartyNickname(guild: Guild, member: Member, channel: TextChannel) {
        val partyConfiguration = configuration[guild.id]?.partyModeConfiguration ?: return

        if (partyConfiguration.channelFilterEnabled && !partyConfiguration.channels.contains(channel.id)) {
            return
        }

        val hasPartyNicknameApplied = member.displayName.endsWith(partyConfiguration.symbol) || member.displayName.endsWith(partyConfiguration.symbolStrip)
        if (!hasPartyNicknameApplied && partyConfiguration.enabled) {
            val nickname = addNickSymbol(member.displayName, partyConfiguration.symbol, partyConfiguration.symbolStrip)
            changeMemberNickname(guild, member, nickname)
        } else if (hasPartyNicknameApplied && !partyConfiguration.enabled) {
            val nickname = removeNickSymbol(member.displayName, mutableListOf(partyConfiguration.symbolStrip))
            changeMemberNickname(guild, member, nickname)
        }
    }

    suspend fun checkNicknameForBlockedSymbols(guild: Guild, member: Member) {
        val guildConfiguration = configuration[guild.id] ?: return

        if (!guildConfiguration.disallowedNicknameSymbols.any { member.displayName.contains(it) }) {
            return
        }

        val nickname = removeNickSymbol(member.displayName, guildConfiguration.disallowedNicknameSymbols)
        loggingService.blocklistedSymbolRemoved(guild, member)
        changeMemberNickname(guild, member, nickname)
    }

    private fun addNickSymbol(nickname: String, symbol: String, stripString: String): String {
        val newName = nickname.replace(stripString, "").replace("\\s+|", "")
        val nickWithPrefix = "$newName $symbol"

        val sizedNick = if (nickWithPrefix.length > 32) {
                nickWithPrefix.substring(0, 31 - symbol.length) + symbol
        } else {
            nickWithPrefix
        }
        val discordNick = sizedNick.replace(" ", "").replace(stripString, "")

        return if (discordNick.isBlank()) {
            "Blanky blankerson"
        } else {
            sizedNick
        }
    }

    private fun removeNickSymbol(nickname: String, disallowedSymbols: MutableList<String>): String {
        var strippedName = nickname
        for (symbol in disallowedSymbols) {
            strippedName = strippedName.replace(symbol, "")
        }
        return if (strippedName.isBlank() || strippedName == "") {
            "Chunck Testa"
        } else {
            strippedName
        }
    }

    private suspend fun changeMemberNickname(guild: Guild, member: Member, nickname: String): Member {
        loggingService.nicknameApplied(guild, member, nickname)
        return member.edit { this.nickname = nickname }
    }
}