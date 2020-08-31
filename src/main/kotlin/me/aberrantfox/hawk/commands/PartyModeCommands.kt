package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands

@CommandSet("Party")
fun createPartymodeCommands(botConfiguration: BotConfiguration) = commands {
    command("toggleParty") {
        description = "Toggles party mode"
        execute {
            botConfiguration.partyMode = !botConfiguration.partyMode
            botConfiguration.save()
            it.respond(if(botConfiguration.partyMode) "Let's get this party started!" else "We're done. That's all folks!")
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
        execute (EveryArg("Suffix")){
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
}