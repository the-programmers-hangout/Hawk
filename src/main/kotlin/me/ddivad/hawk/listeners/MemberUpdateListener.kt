package me.ddivad.hawk.listeners

import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.event.message.MessageCreateEvent
import me.ddivad.hawk.services.NicknameService
import me.jakejmattson.discordkt.dsl.listeners

@Suppress("unused")
fun memberUpdateListener(nicknameService: NicknameService) = listeners {
    on<MemberUpdateEvent> {
        if (this.old?.displayName != this.member.displayName) {
            nicknameService.checkNicknameForBlockedSymbols(getGuild(), member)
        }
    }

    on<MemberJoinEvent> {
        nicknameService.checkNicknameForBlockedSymbols(getGuild(), member)
    }

    on<MessageCreateEvent> {
        val guild = getGuild() ?: return@on
        val member = message.getAuthorAsMember() ?: return@on
        nicknameService.checkNicknameForBlockedSymbols(guild, member)
        nicknameService.setOrRemovePartyNickname(guild, member, message.channel.asChannelOf())
    }
}