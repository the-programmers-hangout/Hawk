package me.aberrantfox.hawk.extensions.jda

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.hawk.botdata.Messages
import me.aberrantfox.hawk.extensions.stdlib.inject
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.getHighestRole
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

fun Member.isStaffMember(guild: Guild, configuration: BotConfiguration) =
        roles.any { it.name == configuration.staffRole } || isOwner

fun Member.isHigherThan(other: Member): Boolean {
    if(this.id == other.id) {
        return false
    }

    if(isOwner) {
        return true
    }

    val ourHighest = getHighestRole()!!.position
    val theirHighest = other.getHighestRole()!!.position

    return ourHighest > theirHighest
}

fun Member.ensureNoHammer(configuration: BotConfiguration, selfMember: Member, action: (Member) -> Unit = {}) {
    if( !(effectiveName.contains(configuration.stripString)) ) {
        return
    }

    if(isHigherThan(selfMember)) {
       return
    }

    val nick = determineNewNickName(configuration)

    modifyNickname(nick).queue {
        action(this)
    }
}

fun Member.ensureHammer(configuration: BotConfiguration, selfMember: Member, messages: Messages, action: (Member) -> Unit = {}) {
    val nameWithoutPrefix = effectiveName.removePrefix(configuration.nickPrefix)

    if( (effectiveName.startsWith(configuration.nickPrefix)) && !(nameWithoutPrefix.contains(configuration.stripString)) ) {
        return
    }

    val nick = determineNewNickName(configuration)

    if(isHigherThan(selfMember) || isOwner) {
        this.user.sendPrivateMessage(messages.STAFF_NICK_PROMPT.inject(nick))
    } else {
        modifyNickname(nick).queue {
            println("Updated ${this.fullName()}'s nickname to include the staff tag.")
        }
    }
}

fun Member.ensureCorrectEffectiveName(guild: Guild, configuration: BotConfiguration, messages: Messages, action: (Member) -> Unit = {}) {
    if (!(configuration.enabled)) {
        return
    }

    if(this.id == guild.selfMember.id) {
        return
    }

    if(this.isStaffMember(guild, configuration)) {
        this.ensureHammer(configuration, guild.selfMember, messages, action)
    } else {
        this.ensureNoHammer(configuration, guild.selfMember, action)
    }
}

fun Member.determineNewNickName(configuration: BotConfiguration) =
    if(isStaffMember(guild, configuration)) {
        applyNickPrefix(effectiveName, configuration.nickPrefix, configuration.stripString)
    } else {
        removeNickPrefix(effectiveName, configuration.stripString)
    }

fun applyNickPrefix(name: String, prefix: String, stripString: String): String {
    val newName = name.replace(stripString, "")
    val nickWithPrefix = "$prefix $newName"

    val sizedNick = if(nickWithPrefix.length > 32) {
        nickWithPrefix.substring(0, 31)
    } else {
        nickWithPrefix
    }

    val discordNick = sizedNick.replace(" ", "").replace(stripString, "")

    return if(discordNick.isBlank()) {
        "$prefix Blanky blankerson"
    } else {
        sizedNick
    }
}

fun removeNickPrefix(name: String, prefix: String): String {
    val strippedName = name.replace(prefix, "")

    return if(strippedName.isBlank() || strippedName == "") {
        "Chunck Testa"
    } else {
        name.replace(prefix, "")
    }
}

