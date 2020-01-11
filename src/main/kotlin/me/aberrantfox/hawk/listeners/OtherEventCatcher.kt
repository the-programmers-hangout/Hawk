package me.aberrantfox.hawk.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.hawk.botdata.Messages
import me.aberrantfox.hawk.extensions.jda.ensureCorrectEffectiveName
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent

class OtherEventCatcher(val configuration: BotConfiguration, val messages: Messages) {
    @Subscribe
    fun onRoleAddedEvent(event: GuildMemberRoleAddEvent) =
        event.member.ensureCorrectEffectiveName(event.guild, configuration, messages)

    @Subscribe
    fun onRoleRemovedEvent(event: GuildMemberRoleRemoveEvent) =
            event.member.ensureCorrectEffectiveName(event.guild, configuration, messages)

    @Subscribe
    fun onMemberJoinedEvent(event: GuildMemberJoinEvent) =
            event.member.ensureCorrectEffectiveName(event.guild, configuration, messages)

    @Subscribe
    fun onGuildMessageEvent(event: GuildMessageReceivedEvent) =
            event.member?.ensureCorrectEffectiveName(event.guild, configuration, messages)

    @Subscribe
    fun onUsernameChanged(event: UserUpdateNameEvent) {
        val guild = event.jda.getGuildById(configuration.guild)!!
        event.user.toMember(guild)?.ensureCorrectEffectiveName(guild, configuration, messages)
    }
}