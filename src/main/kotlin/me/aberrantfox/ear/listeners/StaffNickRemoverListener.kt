package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.ear.extensions.isHigherThan
import me.aberrantfox.ear.extensions.isStaffMember
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent

class StaffNickRemoverListener(val configuration: BotConfiguration) {
    @Subscribe
    fun onGuildMemberNickChange(event: GuildMemberUpdateNicknameEvent) {
        val guild = event.guild
        val member = event.member

        if (!(configuration.enabled)) {
            return
        }

        if (member.isStaffMember(guild, configuration)) {
            return
        }

        if(member.isHigherThan(event.jda.selfUser.toMember(guild)!!)) {
            return
        }

        if(member.isOwner) {
            return
        }

        // Accounting for nulling out their nickname
        val nick = member.nickname ?: return

        if (!(nick.startsWith(configuration.nickPrefix))) {
            return
        }

        println("${member.fullName()} attempted to use $nick as their nickname. Resetting.")

        member.modifyNickname("").queue {
            member.user.sendPrivateMessage("Your nickname made it seem like you were a staff member. This has been automatically logged.")
        }
    }
}