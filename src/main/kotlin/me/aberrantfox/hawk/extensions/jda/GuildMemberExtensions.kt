package me.aberrantfox.hawk.extensions.jda

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.hawk.botdata.Messages
import me.aberrantfox.hawk.extensions.stdlib.inject
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import me.jakejmattson.discordkt.api.extensions.jda.getHighestRole
import me.jakejmattson.discordkt.api.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

fun Member.isStaffMember(guild: Guild, configuration: BotConfiguration) =
        roles.any { it.name == configuration.staffRole } || isOwner

fun Member.isHigherThan(other: Member): Boolean {
    if (this.id == other.id) {
        return false
    }

    if (isOwner) {
        return true
    }

    val ourHighest = getHighestRole().position
    val theirHighest = other.getHighestRole().position

    return ourHighest > theirHighest
}

fun Member.ensureNoHammer(configuration: BotConfiguration, selfMember: Member, action: (Member) -> Unit = {}) {
    if(!configuration.disallowedSymbols.any { effectiveName.contains(it) }) {
        return
    }

    if (isHigherThan(selfMember)) {
        return
    }

    val nick = determineNewNickName(configuration)

    modifyNickname(nick).queue {
        action(this)
    }
}

fun Member.ensureHammer(configuration: BotConfiguration, selfMember: Member, messages: Messages, action: (Member) -> Unit = {}) {
    val nameWithoutPrefix = if (configuration.mode.toLowerCase() == "prefix") {
        effectiveName.removePrefix(configuration.nickSymbol)
    } else {
        effectiveName.removeSuffix(configuration.nickSymbol.replace(" ", ""))
    }

    if (nicknameIsValid(effectiveName, nameWithoutPrefix, configuration)) {
        return
    }

    val nick = determineNewNickName(configuration)

    if (isHigherThan(selfMember) || isOwner) {
        this.user.sendPrivateMessage(messages.STAFF_NICK_PROMPT.inject(nick))
    } else {
        modifyNickname(nick).queue {
            println("Updated ${this.fullName()}'s nickname to include the staff tag.")
        }
    }
}

fun Member.ensureCorrectEffectivePartyName(guild: Guild, configuration: BotConfiguration, messages: Messages, channel: TextChannel, action: (Member) -> Unit = {}) {
    if (!(configuration.enabled)) {
        return
    }

    if (this.id == guild.selfMember.id) {
        return
    }

    if (isHigherThan(guild.selfMember)) {
        return
    }

    if (configuration.partyModeChannelFilter && !configuration.partyModeChannels.contains(channel.id)) {
        return
    }
    
    this.setPartySuffix(configuration)
}

fun Member.ensureCorrectEffectiveName(guild: Guild, configuration: BotConfiguration, messages: Messages, action: (Member) -> Unit = {}) {
    if (!(configuration.enabled)) {
        return
    }

    if (this.id == guild.selfMember.id) {
        return
    }

    if (this.isStaffMember(guild, configuration)) {
        this.ensureHammer(configuration, guild.selfMember, messages, action)
    } else {
        this.ensureNoHammer(configuration, guild.selfMember, action)
    }
}

fun Member.setPartySuffix(configuration: BotConfiguration) {
    val userPartying = effectiveName.endsWith(configuration.partySuffix) || effectiveName.endsWith(configuration.partyStrip)
    if (configuration.partyMode && !userPartying) {
        val nick = applyNickPrefix(effectiveName, configuration.partySuffix, configuration.partyStrip, "suffix")
        modifyNickname(nick).queue {
            println("Added party suffix for ${this.fullName()}")
        }
    } else if (!configuration.partyMode && userPartying){
        val nick = removeNickPrefix(effectiveName, mutableListOf(configuration.partyStrip))
        modifyNickname(nick).queue {
            println("Removed party suffix for ${this.fullName()}")
        }
    }
}

fun Member.determineNewNickName(configuration: BotConfiguration) =
        if (isStaffMember(guild, configuration)) {
            applyNickPrefix(effectiveName, configuration.nickSymbol, configuration.stripString, configuration.mode)
        } else {
            removeNickPrefix(effectiveName, configuration.disallowedSymbols)
        }

fun applyNickPrefix(name: String, symbol: String, stripString: String, mode: String): String {
    val newName = name.replace(stripString, "").replace("\\s+|", "")
    val nickWithPrefix = if (mode.toLowerCase() == "prefix") {
        "$symbol $newName"
    } else {
        "$newName $symbol"
    }

    val sizedNick = if (nickWithPrefix.length > 32) {
        if (mode.toLowerCase() == "prefix") {
            nickWithPrefix.substring(0, 31)
        } else {
            nickWithPrefix.substring(0, 31 - symbol.length) + symbol
        }
    } else {
        nickWithPrefix
    }

    val discordNick = sizedNick.replace(" ", "").replace(stripString, "")

    return if (discordNick.isBlank()) {
        "$symbol Blanky blankerson"
    } else {
        sizedNick
    }
}

fun removeNickPrefix(name: String, disallowedSymbols: MutableList<String>): String {
    var strippedName = name

    for (symbol in disallowedSymbols) {
        strippedName = strippedName.replace(symbol, "")
        println(symbol)
        println("$name $strippedName")
    }

    return if (strippedName.isBlank() || strippedName == "") {
        "Chunck Testa"
    } else {
        strippedName
    }
}

private fun nicknameIsValid(name: String, nameWithoutPrefix: String, configuration: BotConfiguration): Boolean {
    return (configuration.mode == "prefix" && name.startsWith(configuration.nickSymbol)) || name.endsWith(configuration.nickSymbol.replace(" ", ""))
            && !(nameWithoutPrefix.contains(configuration.stripString))
}
