package me.ddivad.hawk.commands

import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.edit
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.embeds.buildHelpEmbed
import me.ddivad.hawk.services.HelpService
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.createMenu

@KordPreview
@Suppress("unused")
fun createInformationCommands(helpService: HelpService) = commands("Utility") {
    val commands = discord.commands
    slash("help") {
        description = "Display help information."
        requiredPermissions = Permissions.EVERYONE
        execute(AnyArg("CommandName", "The command you want to see help for")
            .autocomplete {
                discord.commands
                    .filter { it.hasPermissionToRun(discord, interaction.user, (interaction as GuildAutoCompleteInteraction).getGuild()) }
                    .map { it.names }.flatten()
                    .filter { it.contains(input, true) }
            }
            .optionalNullable(null)) {
            val input = args.first
            if (input.isNullOrBlank()) {
                val event = this
                val message = respond("Help Menu Loading ...")
                channel.createMenu { buildHelpEmbed(event) }
                message?.edit { content = "Help Menu:" }
            } else {
                val cmd = discord.commands.find { command ->
                    command.names.any { it.equals(input, ignoreCase = true) }
                } ?: return@execute
                helpService.sendHelpEmbed(this, cmd)
            }
        }
    }
}