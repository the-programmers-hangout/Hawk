package me.ddivad.hawk.services.nickname.themes

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member

interface NicknameTheme {
    fun hasNicknameApplied(member: Member): Boolean
    suspend fun setNickname(guild: Guild, member: Member)
    suspend fun cleanup(guild: Guild, member: Member)
}