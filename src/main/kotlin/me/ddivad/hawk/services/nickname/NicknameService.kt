package me.ddivad.hawk.services.nickname

import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.FurryNames
import me.ddivad.hawk.dataclasses.PartyModeThemes
import me.ddivad.hawk.services.LoggingService
import me.ddivad.hawk.services.nickname.themes.FurryTheme
import me.ddivad.hawk.services.nickname.themes.SymbolTheme
import me.jakejmattson.discordkt.annotations.Service

@Service
class NicknameService(
    private val configuration: Configuration,
    private val loggingService: LoggingService,
    private val furryNames: FurryNames
) {
    suspend fun setOrRemovePartyNickname(guild: Guild, member: Member, channel: TextChannel) {
        val partyConfiguration = configuration[guild.id]?.partyModeConfiguration ?: return
        if (member.isBot) {
            return
        }
        if (partyConfiguration.channelFilterEnabled && !partyConfiguration.channels.contains(channel.id)) {
            return
        }

        val theme = when (partyConfiguration.theme) {
            PartyModeThemes.SYMBOL -> SymbolTheme(this, configuration)
            PartyModeThemes.FURRY -> FurryTheme(this, furryNames)
        }

        when (partyConfiguration.enabled) {
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