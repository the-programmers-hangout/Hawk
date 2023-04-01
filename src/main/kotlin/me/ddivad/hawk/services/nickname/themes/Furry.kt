package me.ddivad.hawk.services.nickname.themes

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import me.ddivad.hawk.dataclasses.FurryNames
import me.ddivad.hawk.services.nickname.NicknameService

class FurryTheme(private val nicknameService: NicknameService, private val names: FurryNames) : NicknameTheme {
    override fun hasNicknameApplied(member: Member) = names.names.containsAll(member.displayName.split(" "))

    override suspend fun setNickname(guild: Guild, member: Member) {
        if (!hasNicknameApplied(member)) {
            val nickname = names.getRandomName()
            nicknameService.changeMemberNickname(guild, member, nickname)
        }
    }

    override suspend fun cleanup(guild: Guild, member: Member) {
        if (hasNicknameApplied(member)) {
            nicknameService.changeMemberNickname(guild, member, member.username)
        }
    }
}