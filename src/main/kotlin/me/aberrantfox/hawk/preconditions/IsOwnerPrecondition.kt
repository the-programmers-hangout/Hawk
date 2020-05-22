package me.aberrantfox.hawk.preconditions

import me.aberrantfox.hawk.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.Precondition
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.kjdautils.internal.command.precondition

@Precondition
fun isOwner(configuration: BotConfiguration) = precondition { event ->
    if(event.author.id != configuration.owner) Fail("Commands can only be used by the bot owner.") else Pass
}