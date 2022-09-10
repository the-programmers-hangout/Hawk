package me.ddivad.hawk.commands

import dev.kord.core.behavior.edit
import me.ddivad.hawk.arguments.LowerMemberArg
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.services.LoggingService
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField

@Suppress("unused")
fun nicknameCommands(configuration: Configuration, loggingService: LoggingService
) = commands("Nickname") {
    slash("nick", "Set a member's nickname") {
        execute(LowerMemberArg, EveryArg("Nickname")) {
            val (member, nickname) = args
            if (nickname.length > 32) {
                respond("Nickname needs to be < 32 characters")
                return@execute
            }
            member.edit { this.nickname = nickname }
            respond("Nickname set to **$nickname**")
            loggingService.nicknameApplied(guild, member, nickname)
        }
    }

    slash("blocklist", "Add a symbol to the symbol blocklist.") {
        execute(
            ChoiceArg("BloclklistOption", "Add, remove or list options on the blocklist", "add", "remove", "view"),
            AnyArg("symbol", "A word or emoji that you want to disallow").optionalNullable(null)
        ) {
            val(choice, symbol) = args
            val guildConfig = configuration[guild.id] ?: return@execute
            when(choice) {
                "add" -> {
                    if (symbol == null) {
                        respond("Symbol is required")
                        return@execute
                    }
                    if (guildConfig.disallowedNicknameSymbols.contains(symbol)) {
                        respond("$symbol is already blacklisted")
                        return@execute
                    }
                    guildConfig.disallowedNicknameSymbols.add(symbol.replace(" ", ""))
                    configuration.save()
                    respondPublic("Added **$symbol** to blacklist.")
                }
                "remove" -> {
                    if (symbol == null) {
                        respond("Symbol is required")
                        return@execute
                    }
                    if (!guildConfig.disallowedNicknameSymbols.contains(symbol)) {
                        respond("$symbol is not blacklisted")
                        return@execute
                    }
                    guildConfig.disallowedNicknameSymbols.remove(symbol.replace(" ", ""))
                    configuration.save()
                    respondPublic("Removed **$symbol** from blacklist.")

                }
                "view" -> {
                    respondPublic {
                        color = discord.configuration.theme
                        addField("**Blocklisted Symbols**", guildConfig.disallowedNicknameSymbols.joinToString(" , ") )
                    }
                }
            }
        }
    }
}