package me.ddivad.hawk.commands

import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.PartyModeThemes
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.dsl.edit

@Suppress("unused")
fun partyModeCommands(configuration: Configuration) = subcommand("Party") {
    sub("getPartySymbol", "View the current party mode symbol") {
        execute {
            val guildConfiguration = configuration[guild.id] ?: return@execute
            respondPublic("Current Symbol: ${guildConfiguration.partyModeConfiguration.symbol}")
        }
    }

    sub("toggleParty", "Toggles party mode") {
        execute(autocompleteModeArg()) {
            val choice = args.first
            val guildConfiguration = configuration[guild.id] ?: return@execute
            if (choice == null) {
                if (guildConfiguration.partyModeConfiguration.enabled) {
                    configuration.edit { guildConfiguration.partyModeConfiguration.enabled = false }
                    respondPublic("We're done. That's all folks!")
                    return@execute
                } else {
                    respond("**Mode** is required when enabling a party")
                    return@execute
                }
            } else {
                val mode = PartyModeThemes.valueOf(choice)
                configuration.edit {
                    guildConfiguration.partyModeConfiguration.theme = mode
                    guildConfiguration.partyModeConfiguration.enabled = !guildConfiguration.partyModeConfiguration.enabled
                }
                respondPublic("Let's get this **${mode.name.lowercase()}** party started!")
            }
        }
    }

    sub("setPartySymbol", "Set new party mode symbol") {
        execute(EveryArg("Suffix")) {
            val symbol = args.first.replace("\uD83D\uDD28", "")
            val guildConfiguration = configuration[guild.id] ?: return@execute

            if (symbol.length > 11 || symbol.isEmpty()) {
                respond("Suffix is must be shorter than 11 characters and not empty!")
            } else {
                guildConfiguration.partyModeConfiguration.symbol = "$symbol "
                guildConfiguration.partyModeConfiguration.symbolStrip = symbol
                configuration.save()
                respondPublic("Set the party suffix to **${symbol}**")
            }
        }
    }

    sub("partyChannelFilter", "Add, remove or view channels used in party mode") {
        execute(
            ChoiceArg("PartyChannelOption", "Party channel options", "enable", "disable", "add", "remove", "view"),
            ChannelArg<TextChannel>("PartyChannel", "A channel where party mode will apply").optionalNullable(null)
        ) {
            val (choice, channel) = args
            val guildConfiguration = configuration[guild.id] ?: return@execute

            when (choice) {
                "enable" -> {
                    guildConfiguration.partyModeConfiguration.channelFilterEnabled = true
                    configuration.save()
                    respondPublic("Channel filter **enabled**")
                }
                "disable" -> {
                    guildConfiguration.partyModeConfiguration.channelFilterEnabled = false
                    configuration.save()
                    respondPublic("Channel filter **disabled**")
                }
                "add" -> {
                    if (channel == null) {
                        respond("Received less arguments than expected. Expected: `(Channel)`")
                        return@execute
                    }
                    if (guildConfiguration.partyModeConfiguration.channels.contains(channel.id)) {
                        respondPublic("${channel.mention} is already included in the party mode channels")
                        return@execute
                    }

                    guildConfiguration.partyModeConfiguration.channels.add(channel.id)
                    configuration.save()
                    respondPublic("**${channel.mention}** is invited to the party!")
                }
                "remove" -> {
                    if (channel == null) {
                        respondPublic("Received less arguments than expected. Expected: `(Channel)`")
                        return@execute
                    }
                    if (!guildConfiguration.partyModeConfiguration.channels.contains(channel.id)) {
                        respondPublic("${channel.mention} is not included in the party mode channels")
                        return@execute
                    }

                    guildConfiguration.partyModeConfiguration.channels.remove(channel.id)
                    configuration.save()
                    respondPublic("Party invitation removed for **${channel.mention}**")
                }
                "view" -> {
                    respondPublic {
                        color = discord.configuration.theme
                        title = "Party Mode Channels"
                        field {
                            name = "Channels"
                            value = guildConfiguration.partyModeConfiguration.channels
                                .map { discord.kord.getChannel(it)?.mention }
                                .joinToString("\n")
                        }
                    }
                }
            }
        }
    }
}

fun autocompleteModeArg() = AnyArg("Mode", "Party Mode").autocomplete {
    PartyModeThemes.values().joinToString(", ").split(", ")
}.optionalNullable(null)