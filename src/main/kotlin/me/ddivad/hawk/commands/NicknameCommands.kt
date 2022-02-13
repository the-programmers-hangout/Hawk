package me.ddivad.hawk.commands

import dev.kord.core.behavior.edit
import me.ddivad.hawk.arguments.LowerMemberArg
import me.ddivad.hawk.dataclasses.Configuration
import me.ddivad.hawk.dataclasses.Permissions
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField

@Suppress("unused")
fun nicknameCommands(configuration: Configuration) = commands("Nickname") {
    slash("nick") {
        description = "Set a member's nickname"
        requiredPermission = Permissions.STAFF
        execute(LowerMemberArg, EveryArg("Nickname")) {
            val (member, nickname) = args
            if (nickname.length > 32) {
                respond("Nickname needs to be < 32 characters")
                return@execute
            }
            member.edit { this.nickname = nickname }
            respond("Nickname set to **$nickname**")
        }
    }

    slash("blocklist") {
        description = "Add a symbol to the symbol blocklist."
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
                    respond("Added **$symbol** to blacklist.", false)
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
                    respond("Removed **$symbol** from blacklist.", false)

                }
                "view" -> {
                    respond(false) {
                        color = discord.configuration.theme
                        addField("**Blocklisted Symbols**", guildConfig.disallowedNicknameSymbols.joinToString(" , ") )
                    }
                }
            }
        }
    }
}