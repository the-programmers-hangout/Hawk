package me.aberrantfox.ear.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.getHighestRole
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent

class StaffNickEnforcerListener(val configuration: BotConfiguration) {
    @Subscribe
    fun onGuildNickChange(event: GuildMemberUpdateNicknameEvent) {
        if( !(configuration.enabled) ) {
            return
        }

        val guild = event.jda.getGuildById(configuration.guild)!!
        val member = event.user.toMember(guild) ?: return

        val isStaffMember = member.roles.any { it.name == configuration.staffRole }

        if(!isStaffMember) {
            return
        }

        // Maybe they deleted their nickname
        val nick = member.nickname ?: return

        if( (nick.startsWith(configuration.nickPrefix)) ) {
            return
        }

        val newNick = applyNickPrefix(nick, configuration.nickPrefix)

        // !! operator is safe because to be at this point they must have a staff role at least.
        // !! is assumed for the bot.

        val memberLevel = member.getHighestRole()!!.position
        val userLevel = event.jda.selfUser.toMember(guild)!!.getHighestRole()!!.position

        val isHigher = memberLevel > userLevel

        if(isHigher) {
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