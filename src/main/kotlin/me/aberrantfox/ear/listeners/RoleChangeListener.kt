package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent

class RoleChangeListener(val configuration: BotConfiguration) {

    @Subscribe
    fun onRoleRemovedEvent(event: GuildMemberRoleRemoveEvent) {
        if(event.roles.any { it.name == configuration.staffRole }) {
            event.member.modifyNickname("").queue()
        }
    }

}