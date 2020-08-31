package me.aberrantfox.hawk.commands

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.command.commands

@CommandSet("Admin")
fun createAdministrationCommands(botConfiguration: BotConfiguration) = commands {
    command("enable") {
        description = "Enable the bot"
        execute {
            botConfiguration.enabled = true
            botConfiguration.save()
            it.respond("Enabled the bot. Listening.")
        }
    }

    command("disable") {
        description = "Disable the bot"
        execute {
            botConfiguration.enabled = false
            botConfiguration.save()
            it.respond("Disabled the bot. Events will be ignored as well as commands other than toggle/enable/disable.")
        }
    }

    command("setAdminRole") {
        description = "Set the admin role for the bot."
        execute(RoleArg) {
            botConfiguration.adminRole = it.args.first.name
            botConfiguration.save()
            it.respond("Set the admin role to **${it.args.first.name}**")
        }
    }
}