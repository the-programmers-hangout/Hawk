package me.ddivad.hawk.services

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.FurryNames
import me.ddivad.hawk.dataclasses.PartyModeThemes
import me.jakejmattson.discordkt.annotations.Service

@Service
class NicknameService(private val configuration: Configuration, private val loggingService: LoggingService, private val furryNames: FurryNames) {
    suspend fun setOrRemovePartyNickname(guild: Guild, member: Member, channel: TextChannel) {
        val partyConfiguration = configuration[guild.id]?.partyModeConfiguration ?: return
        if (member.isBot) {
            return
        }
        if (partyConfiguration.channelFilterEnabled && !partyConfiguration.channels.contains(channel.id)) {
            return
        }

        val theme = when (partyConfiguration.mode) {
            PartyModeThemes.SYMBOL ->  SymbolNicknameTheme(this, configuration)
            PartyModeThemes.FURRY -> FurryNicknameTheme(this, furryNames)
        }
        
        when(partyConfiguration.enabled) {
            true -> theme.setNickname(guild, member)
            false -> theme.cleanup(guild, member)
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

    fun addNickSymbol(nickname: String, symbol: String, stripString: String): String {
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

    fun removeNickSymbol(nickname: String, disallowedSymbols: MutableList<String>): String {
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

    suspend fun changeMemberNickname(guild: Guild, member: Member, nickname: String): Member {
        loggingService.nicknameApplied(guild, member, nickname)
        return member.edit { this.nickname = nickname }
    }
}

interface PartyModeTheme {
    fun hasNicknameApplied(member: Member): Boolean
    suspend fun setNickname(guild: Guild, member: Member)
    suspend fun cleanup(guild: Guild, member: Member)
}

class FurryNicknameTheme(private val nicknameService: NicknameService, private val furryNames: FurryNames): PartyModeTheme {
    override fun hasNicknameApplied(member: Member) = furryNames.names.containsAll(member.displayName.split(" "))

    override suspend fun setNickname(guild: Guild, member: Member) {
        if (!hasNicknameApplied(member)) {
            val nickname = furryNames.getRandomName()
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }

    override suspend fun cleanup(guild: Guild, member: Member) {
        if (hasNicknameApplied(member)) {
            nicknameService.changeMemberNickname(guild, member, member.username)
        }
    }
}

class SymbolNicknameTheme(private val nicknameService: NicknameService, private val configuration: Configuration): PartyModeTheme {
    private fun getPartyConfiguration(member: Member) = configuration[member.guildId]?.partyModeConfiguration
    override fun hasNicknameApplied(member: Member): Boolean {
        val partyConfiguration =  getPartyConfiguration(member) ?: return false
        return member.displayName.endsWith(partyConfiguration.symbol) || member.displayName.endsWith(partyConfiguration.symbolStrip)
    }

    override suspend fun setNickname(guild: Guild, member: Member) {
        val partyConfiguration = getPartyConfiguration(member) ?: return
        if (!hasNicknameApplied(member)) {
            val nickname = nicknameService.addNickSymbol(member.displayName, partyConfiguration.symbol, partyConfiguration.symbolStrip)
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }

    override suspend fun cleanup(guild: Guild, member: Member) {
        val partyConfiguration =  getPartyConfiguration(member) ?: return
        if (hasNicknameApplied(member)) {
            val nickname = nicknameService.removeNickSymbol(member.displayName, mutableListOf(partyConfiguration.symbolStrip))
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }
}