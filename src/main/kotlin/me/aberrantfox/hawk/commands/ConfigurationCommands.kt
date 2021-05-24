package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.dsl.embed.embed
import net.dv8tion.jda.api.entities.MessageEmbed

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

    command("blacklist") {
        description = "Add a symbol to the symbol blacklist."
        execute(ChoiceArg("add/rem/list", "add", "rem", "view"),
            AnyArg("symbol").makeNullableOptional(null)) {
            val(choice, symbol) = it.args
            when(choice) {
                "add" -> {
                    if (botConfiguration.disallowedSymbols.contains(symbol)) {
                        it.respond("${symbol} is already blacklisted")
                        return@execute
                    }
                    botConfiguration.disallowedSymbols.add(symbol!!.replace(" ", ""))
                    botConfiguration.save()
                    it.respond("Added **${symbol}** to blacklist.")
                }

                "rem" -> {
                    if (!botConfiguration.disallowedSymbols.contains(symbol)) {
                        it.respond("${symbol} is not blacklisted")
                        return@execute
                    }
                    botConfiguration.disallowedSymbols.remove(symbol!!.replace(" ", ""))
                    botConfiguration.save()
                    it.respond("Removed **${symbol}** from blacklist.")

                }

                "view" -> {
                    it.respond(embed {
                        color = infoColor
                        addField("**Blacklisted Symbols**", botConfiguration.disallowedSymbols.joinToString(" , ") )
                    })
                }
                else -> {
                    it.respond("Invalid choice")
                }
            }
        }
    }
}
