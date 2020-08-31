package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands

@CommandSet("Config")
fun createConfigCommands(botConfiguration: BotConfiguration) = commands {
    command("setMode") {
        description = "Set the mode to prefix or suffix"
        execute(ChoiceArg("mode", "prefix", "suffix")) {
            botConfiguration.mode = it.args.first
            botConfiguration.save()
            it.respond("Set the bots nickname mode to **${it.args.first}**.")
        }
    }

    command("setSymbol") {
        description = "Set the token to appear in nicknames."
        execute(AnyArg("Symbol")) {
            val symbol = it.args.first
            botConfiguration.nickSymbol = "$symbol "
            botConfiguration.stripString = symbol
            botConfiguration.save()
            it.respond("Set the bots symbol to **${it.args.first}**")
        }
    }

    command("setRole") {
        description = "Set the role that will have symbols enforced."
        execute(RoleArg) {
            botConfiguration.staffRole = it.args.first.name
            botConfiguration.save()
            it.respond("Set the bots role to **${it.args.first.name}**")
        }
    }

    command("setPrefix") {
        description = "Set the bot's invocation prefix"
        execute(AnyArg("Prefix")) {
            botConfiguration.botPrefix = it.args.first
            botConfiguration.save()
            it.respond("Set the bots owner prefix to **${it.args.first}**")
        }
    }
}
