package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.dsl.command.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands

@CommandSet("Admin")
fun createAdministrationCommands(botConfiguration: BotConfiguration) = commands {
    command("toggleFunctionality", "toggle") {
        description = "Toggles the bot functionality"
        execute {
            botConfiguration.enabled = !botConfiguration.enabled
            it.respond("Toggled Bot Status: ${botConfiguration.enabled}")
        }
    }

    command("enable") {
        description = "Enable the bot"
        execute {
            botConfiguration.enabled = true
            it.respond("Enabled the bot. Listening.")
        }
    }

    command("disable") {
        description = "Disable the bot"
        execute {
            botConfiguration.enabled = false
            it.respond("Disabled the bot. Events will be ignored as well as commands other than toggle/enable/disable.")
        }
    }
}