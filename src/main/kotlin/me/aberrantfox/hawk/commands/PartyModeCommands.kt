package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.dsl.embed.embed

@CommandSet("Party")
fun createPartymodeCommands(botConfiguration: BotConfiguration) = commands {
    command("toggleParty") {
        description = "Toggles party mode"
        execute {
            botConfiguration.partyMode = !botConfiguration.partyMode
            botConfiguration.save()
            it.respond(if (botConfiguration.partyMode) "Let's get this party started! 🥳" else "We're done. That's all folks!")
        }
    }

    command("getPartySuffix") {
        description = "Display current party mode suffix"
        execute {
            it.respond("Suffix: ${botConfiguration.partySuffix}")
        }
    }

    command("setPartySuffix") {
        description = "Set new party mode suffix"
        execute(EveryArg("Suffix")) {
            val symbol = it.args.first.replace("\uD83D\uDD28", "")
            if (symbol.length > 11 || symbol.isEmpty()) {
                it.respond("Suffix is must be shorter than 11 characters and not empty!")
            } else {
                botConfiguration.partySuffix = "$symbol "
                botConfiguration.partyStrip = symbol
                botConfiguration.save()
                it.respond("Set the party suffix to **${symbol}**")
            }
        }
    }

    command("partyChannels") {
        description = "Add, remove or view channels used in party mode"
        execute(ChoiceArg("add/rem/list", "add", "rem", "list"),
                TextChannelArg.makeNullableOptional(null)) {
            val(choice, channel) = it.args

            when(choice) {
                "add" -> {
                    if (channel == null) return@execute it.respond("Received less arguments than expected. Expected: `(Channel)`")

                    botConfiguration.partyModeChannels.add(channel.id)
                    botConfiguration.save()
                    it.respond("**${channel.asMention}** is invited to the party!")
                }

                "rem" -> {
                    if (channel == null) return@execute it.respond("Received less arguments than expected. Expected: `(Channel)`")

                    botConfiguration.partyModeChannels.remove(channel.id)
                    botConfiguration.save()
                    it.respond("Party invitation removed for **${channel.asMention}**")
                }

                "list" -> {
                    val jda = it.discord.jda
                    it.respond(embed {
                        color = infoColor
                        simpleTitle = "Party Mode Channels"
                        field {
                            name = "Channels"
                            value = ""
                            botConfiguration.partyModeChannels.forEach {
                                value += jda.getTextChannelById(it)?.asMention + "\n"
                            }
                        }
                    })
                }
                else -> {
                    it.respond("Invalid choice")
                }
            }
        }
    }

    command("togglePartyChannels") {
        description = "Toggle channel based filtering for party mode"
        execute {
            botConfiguration.partyModeChannelFilter = !botConfiguration.partyModeChannelFilter
            botConfiguration.save()
            it.respond("Party mode channel filtering **${if(botConfiguration.partyModeChannelFilter) "enabled" else "disabled"}**")
        }
    }
}
