package me.aberrantfox.ear.extensions

import me.aberrantfox.ear.configuration.BotConfiguration
import me.aberrantfox.kjdautils.extensions.jda.getHighestRole
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

fun Member.isStaffMember(guild: Guild, configuration: BotConfiguration) =
        roles.any { it.name == configuration.staffRole }

fun Member.isHigherThan(other: Member): Boolean {
    val ourHighest = getHighestRole()!!.position
    val theirHighest = other.getHighestRole()!!.position

    return ourHighest > theirHighest
}