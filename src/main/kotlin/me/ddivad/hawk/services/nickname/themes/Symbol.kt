package me.ddivad.hawk.services.nickname.themes

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.services.nickname.NicknameService

class SymbolTheme(private val nicknameService: NicknameService, private val configuration: Configuration) : NicknameTheme {
    private fun getPartyConfiguration(member: Member) = configuration[member.guildId]?.partyModeConfiguration
    override fun hasNicknameApplied(member: Member): Boolean {
        val partyConfiguration = getPartyConfiguration(member) ?: return false
        return member.displayName.endsWith(partyConfiguration.symbol) || member.displayName.endsWith(partyConfiguration.symbolStrip)
    }

    override suspend fun setNickname(guild: Guild, member: Member) {
        val partyConfiguration = getPartyConfiguration(member) ?: return
        if (!hasNicknameApplied(member)) {
            val nickname = nicknameService.addNickSymbol(
                member.displayName,
                partyConfiguration.symbol,
                partyConfiguration.symbolStrip
            )
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }

    override suspend fun cleanup(guild: Guild, member: Member) {
        val partyConfiguration = getPartyConfiguration(member) ?: return
        if (hasNicknameApplied(member)) {
            val nickname =
                nicknameService.removeNickSymbol(member.displayName, mutableListOf(partyConfiguration.symbolStrip))
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }
}