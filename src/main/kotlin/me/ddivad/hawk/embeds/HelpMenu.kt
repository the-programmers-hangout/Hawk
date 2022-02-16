package me.ddivad.hawk.embeds

import dev.kord.common.entity.ButtonStyle
import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.Emojis
import me.jakejmattson.discordkt.commands.Command
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.dsl.MenuBuilder

suspend fun MenuBuilder.buildHelpEmbed(event: CommandEvent<*>) {
    val container = event.discord.commands
    fun joinNames(value: List<Command>) =
        value.joinToString("\n") { it.names.first() }

    val groupedCommands = container
        .filter { it.hasPermissionToRun(event) }
        .groupBy { it.category }
        .toList()
        .sortedByDescending { it.second.size }

    val categoryNames = container
        .filter { it.hasPermissionToRun(event) }
        .groupBy { it.category }
        .toList()
        .sortedByDescending { it.second.size }
        .map { it.first }
        .toList()

    if (groupedCommands.isNotEmpty()) {
        groupedCommands.map { (category, commands) ->
            val sorted = commands
                .sortedBy { it.names.first() }

            page {
                title = "Help - $category Commands"
                description = """
                        You have **${commands.size}** commands available based on permissions.

                        Use `${event.prefix()}help` <command> for more information
                    """.trimIndent()
                color = event.discord.configuration.theme

                field {
                    name = "**Commands**"
                    value = "```css\n${joinNames(sorted)}\n```"
                    inline = true
                }
            }
        }

        categoryNames.chunked(5).forEachIndexed { index, category ->
            buttons {
                category.forEachIndexed { page, name ->
                    button(name, getEmojiForCategory(name), ButtonStyle.Secondary) {
                        loadPage(page + index * 5)
                    }
                }
            }
        }
    }
}

private fun getEmojiForCategory(categoryName: String): DiscordEmoji.Generic? {
    return when (categoryName) {
        "Configuration" -> Emojis.hammerAndPick
        "Utility" -> Emojis.toolbox
        "Party" -> Emojis.partyingFace
        "Nickname" -> Emojis.clipboard
        "ReactionRole" -> Emojis.whiteCheckMark
        else -> null
    }
}