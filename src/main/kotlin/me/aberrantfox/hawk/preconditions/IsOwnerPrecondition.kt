package me.aberrantfox.hawk.preconditions

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.jakejmattson.discordkt.api.dsl.preconditions.Precondition
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.dsl.preconditions.Fail
import me.jakejmattson.discordkt.api.dsl.preconditions.Pass
import me.jakejmattson.discordkt.api.dsl.preconditions.PreconditionResult

class isOwnerPrecondition(private val configuration: BotConfiguration): Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        if(event.author.id != configuration.owner) {
            return Fail("Commands can only be used by the bot owner.")
        }
        return Pass
    }
}