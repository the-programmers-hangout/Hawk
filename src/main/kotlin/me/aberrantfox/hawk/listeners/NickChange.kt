package me.aberrantfox.hawk.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.hawk.botdata.Messages
import me.aberrantfox.hawk.extensions.jda.ensureCorrectEffectiveName
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent

class NickChange(val configuration: BotConfiguration, val messages: Messages) {
    @Subscribe
    fun onGuildMemberNickChange(event: GuildMemberUpdateNicknameEvent) =
        event.member.ensureCorrectEffectiveName(event.guild, configuration, messages)
}