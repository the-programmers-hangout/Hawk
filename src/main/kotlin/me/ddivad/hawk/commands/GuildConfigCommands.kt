package me.ddivad.hawk.commands

import dev.kord.core.entity.channel.TextChannel
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.ddivad.hawk.embeds.createConfigurationEmbed
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.commands.subcommand
import java.util.*

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = subcommand("Configuration", Permissions.ADMINISTRATOR) {
    sub("setup", "Setup a configuration for this guild") {
        execute {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
                respond(
                    "Created new configuration for guild **${guild.name}**." +
                            "Please run `/setRole` to set the staff and admin roles to finalize configuration"
                )
                return@execute
            }
            respond("Guild configuration already exists")
        }
    }

    sub("setChannel", "Set the logging or alert channel") {
        execute(
            ChoiceArg("ChannelChoice", "Set the logging or alert channel", "logging", "alert"),
            ChannelArg<TextChannel>("Channel", "Channel that messages will be posted to")
        ) {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
            }
            val (choice, channel) = args
            when (choice) {
                "logging" -> {
                    configuration[guild.id]?.loggingConfiguration?.logChannel = channel.id
                }
                "alert" -> {
                    configuration[guild.id]?.loggingConfiguration?.alertChannel = channel.id
                }
            }
            configuration.save()
            respondPublic("${choice.replaceFirstChar { it.titlecase(Locale.getDefault()) }} channel set to: **${channel.mention}**")
        }
    }

    sub("view", "View the bot configuration", Permissions.STAFF) {
        execute {
            respondPublic {
                createConfigurationEmbed(configuration, guild, discord)
            }
        }
    }
}