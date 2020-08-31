package me.aberrantfox.hawk.preconditions

import me.aberrantfox.hawk.extensions.discordKt.requiredPermissionLevel
import me.aberrantfox.hawk.services.DEFAULT_REQUIRED_PERMISSION
import me.aberrantfox.hawk.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.dsl.preconditions.Fail
import me.jakejmattson.discordkt.api.dsl.preconditions.Pass
import me.jakejmattson.discordkt.api.dsl.preconditions.Precondition
import me.jakejmattson.discordkt.api.dsl.preconditions.PreconditionResult
import me.jakejmattson.discordkt.api.extensions.jda.toMember

class PermissionPrecondtion(private val permissionsService: PermissionsService): Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command
        val requiredPermissionLevel = command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
        val guild = event.guild!!
        val member = event.author.toMember(guild)!!

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail("You do not have the required permissions to perform this action.")

        return Pass
    }
}