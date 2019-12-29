package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.ear.extensions.isHigherThan
import me.aberrantfox.ear.extensions.isStaffMember
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent

class StaffNickEnforcerListener(private val configuration: BotConfiguration) {
    @Subscribe
    fun onGuildNickChange(event: GuildMemberUpdateNicknameEvent) {
        if(event.user.id == event.jda.selfUser.id) {
            return
        }

        val guild = event.guild
        val member = event.member

        if( !(configuration.enabled) ) {
            return
        }

        if( !(member.isStaffMember(guild, configuration))) {
            return
        }

        // Maybe they deleted their nickname
        val nick = member.nickname ?: return

        if( (nick.startsWith(configuration.nickPrefix)) ) {
            return
        }

        val newNick = applyNickPrefix(nick, configuration.nickPrefix)
        val botUser = event.jda.selfUser.toMember(guild)!!

        if(member.isHigherThan(botUser) || member.isOwner) {
            member.user.sendPrivateMessage("You have updated your nickname and it does not contain the prefix." +
                    " Here it is with the prefix: `$newNick`")
        } else {
            member.modifyNickname(newNick).queue {
                println("Updated ${member.fullName()}'s nickname to include the staff tag.")
            }
        }
    }
}

private fun applyNickPrefix(name: String, prefix: String): String {
    val nickWithPrefix = "$prefix $name"

    return if(nickWithPrefix.length > 32) {
        nickWithPrefix.substring(0, 31)
    } else {
        nickWithPrefix
    }
}