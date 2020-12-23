package me.ddivad.hawk.core.listeners

import com.gitlab.kordlib.core.event.UserUpdateEvent
import com.gitlab.kordlib.core.event.guild.MemberJoinEvent
import com.gitlab.kordlib.core.event.guild.MemberUpdateEvent
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import me.ddivad.hawk.core.services.NicknameService
import me.jakejmattson.discordkt.api.dsl.listeners

fun enforceNicknameListeners(nicknameService: NicknameService) = listeners {
    on<MemberUpdateEvent> {

    }

    on<MemberJoinEvent> {

    }

    on<MessageCreateEvent> {

    }

    on<UserUpdateEvent> {

    }
}