package me.ddivad.hawk.commands

import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.Image
import dev.kord.x.emoji.Emojis
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.commands.commands
import java.util.*

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = commands("Configuration") {
    slash("setup") {
        description = "Setup a configuration for this guild"
        requiredPermissions = Permissions.ADMINISTRATOR
        execute {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
                respond("Created new configuration for guild **${guild.name}**." +
                        "Please run `/setRole` to set the staff and admin roles to finalize configuration"
                )
                return@execute
            }
            respond("Guild configuration already exists")
        }
    }

    slash("setPrefix") {
        description = "Set the bot prefix."
        requiredPermissions = Permissions.ADMINISTRATOR
        execute(EveryArg) {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
            }
            val prefix = args.first
            configuration[guild.id]?.prefix = prefix
            configuration.save()
            respondPublic("Prefix set to: **$prefix**")
        }
    }

    slash("setRole") {
        description = "Set the bot staff or admin role."
        requiredPermissions = Permissions.ADMINISTRATOR
        execute(ChoiceArg("RoleChoice", "Set the staff or admin role", "staff", "admin"), RoleArg("RoleName")) {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
            }
            val (choice, role) = args
            when(choice) {
                "staff" -> {
                    configuration[guild.id]?.staffRoleId = role.id
                }
                "admin" -> {
                    configuration[guild.id]?.adminRoleId = role.id
                }
            }
            configuration.save()
            respondPublic("${choice.replaceFirstChar { it.titlecase(Locale.getDefault()) }} role set to: **${role.name}**")
        }
    }

    slash("setChannel") {
        description = "Set the logging or alert channel"
        requiredPermissions = Permissions.ADMINISTRATOR
        execute(ChoiceArg("ChannelChoice", "Set the logging or alert channel", "logging", "alert"),
            ChannelArg<TextChannel>("Channel", "Channel that messages will be posted to")) {
            if (!configuration.hasGuildConfig(guild.id)) {
                configuration.setup(guild)
            }
            val (choice, channel) = args
            when(choice) {
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

    slash("configuration") {
        description = "View the bot configuration"
        requiredPermissions = Permissions.STAFF
        execute {
            val guildConfiguration = configuration[guild.id] ?: return@execute
            val fullyConfigured = configuration.isFullyConfigured(guild)
            respondPublic {
                color = discord.configuration.theme
                title = "Configuration"
                thumbnail {
                    url = guild.getIconUrl(Image.Format.PNG) ?: ""
                }

                description = "Fully Configured: ${if(fullyConfigured) Emojis.whiteCheckMark else Emojis.x}"

                field {
                    name = "**General**"
                    value = "Prefix: ${guildConfiguration.prefix}\n" +
                            "Admin Role: ${guildConfiguration.adminRoleId?.let { guild.getRole(it).mention }}\n" +
                            "Staff Role: ${guildConfiguration.staffRoleId?.let { guild.getRole(it).mention }}\n" +
                            "Logging Channel: ${guildConfiguration.loggingConfiguration.logChannel?.let { guild.getChannel(it).mention }}\n" +
                            "Alert Channel: ${guildConfiguration.loggingConfiguration.alertChannel?.let { guild.getChannel(it).mention }}\n"
                }

                field {
                    name = "**Party**"
                    value = "Enabled: ${guildConfiguration.partyModeConfiguration.enabled}\n" +
                            "Symbol: ${guildConfiguration.partyModeConfiguration.symbol}\n" +
                            "Channel Filter Enabled: ${guildConfiguration.partyModeConfiguration.channelFilterEnabled}\n" +
                            "Party Channels: ${guildConfiguration.partyModeConfiguration.channels.map {guild.getChannel(it).mention}.joinToString(", ") }"
                }
            }
        }
    }
}