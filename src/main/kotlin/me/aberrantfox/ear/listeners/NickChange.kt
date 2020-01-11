package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.ear.data.Messages
import me.aberrantfox.ear.extensions.jda.ensureCorrectEffectiveName
import me.aberrantfox.ear.extensions.jda.ensureNoHammer
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent

class NickChange(val configuration: BotConfiguration, val messages: Messages) {
    @Subscribe
    fun onGuildMemberNickChange(event: GuildMemberUpdateNicknameEvent) =
        event.member.ensureCorrectEffectiveName(event.guild, configuration, messages)
}