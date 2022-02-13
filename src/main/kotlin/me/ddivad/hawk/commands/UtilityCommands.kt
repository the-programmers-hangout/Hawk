package me.ddivad.hawk.commands

import dev.kord.common.annotation.KordPreview
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.services.HelpService
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.commands

@KordPreview
@Suppress("unused")
fun createInformationCommands(helpService: HelpService) = commands("Utility") {
    command("help") {
        description = "Display help information."
        requiredPermission = Permissions.NONE
        execute(AnyArg("Command").optionalNullable()) {
            val input = args.first
            if (input.isNullOrBlank()) {
                helpService.buildHelpEmbed(this)
            } else {
                val cmd = discord.commands.find { command ->
                    command.names.any { it.equals(input, ignoreCase = true) }
                } ?: return@execute
                helpService.sendHelpEmbed(this, cmd)
            }
        }
    }
}