package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.ear.data.Messages
import me.aberrantfox.ear.extensions.jda.ensureCorrectEffectiveName
import me.aberrantfox.ear.extensions.jda.ensureHammer
import me.aberrantfox.ear.extensions.jda.ensureNoHammer
import me.aberrantfox.ear.extensions.jda.isStaffMember
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.events.guild.member.update.GenericGuildMemberUpdateEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.user.update.GenericUserUpdateEvent
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