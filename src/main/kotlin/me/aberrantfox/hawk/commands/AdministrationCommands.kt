package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.services.PersistenceService

@CommandSet("Admin")
fun createAdministrationCommands(botConfiguration: BotConfiguration, persistenceService: PersistenceService) = commands {
    command("toggleFunctionality", "toggle") {
        description = "Toggles the bot functionality"
        execute {
            botConfiguration.enabled = !botConfiguration.enabled
            persistenceService.save(botConfiguration)
            it.respond("Bot has been turned **${if(botConfiguration.enabled) "On" else "Off"}**")
        }
    }

    command("enable") {
        description = "Enable the bot"
        execute {
            botConfiguration.enabled = true
            persistenceService.save(botConfiguration)
            it.respond("Enabled the bot. Listening.")
        }
    }

    command("disable") {
        description = "Disable the bot"
        execute {
            botConfiguration.enabled = false
            persistenceService.save(botConfiguration)
            it.respond("Disabled the bot. Events will be ignored as well as commands other than toggle/enable/disable.")
        }
    }

    command("setMode") {
        description = "Set the mode to prefix or suffix"
        execute(ChoiceArg("mode", "prefix", "suffix")) {
            botConfiguration.mode = it.args.first
            persistenceService.save(botConfiguration)
            it.respond("Set the bots nickname mode to **${it.args.first}**.")
        }
    }

    command("setNickSymbol") {
        description = "Set the token to appear in nicknames."
        execute(SentenceArg) {
            val symbol = it.args.first
            botConfiguration.nickSymbol = "$symbol "
            botConfiguration.stripString = symbol
            persistenceService.save(botConfiguration)
            it.respond("Set the bots symbol to **${it.args.first}**")
        }
    }
}