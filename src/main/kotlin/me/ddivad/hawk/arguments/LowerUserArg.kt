package me.ddivad.hawk.arguments

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.arguments.Error
import me.jakejmattson.discordkt.arguments.Result
import me.jakejmattson.discordkt.arguments.Success
import me.jakejmattson.discordkt.arguments.UserArgument
import me.jakejmattson.discordkt.commands.DiscordContext

open class LowerMemberArg(
    override val name: String = "LowerMemberArg",
    override val description: String = "A Member with a lower rank",
    private val allowsBot: Boolean = false
) : UserArgument<Member> {
    companion object : LowerMemberArg()

    override suspend fun transform(input: User, context: DiscordContext): Result<Member> {
        val guild = context.guild ?: return Error("No guild found")
        val targetMember = input.asMemberOrNull(guild.id) ?: return Error("Member not found.")
        val authorAsMember = context.author.asMemberOrNull(guild.id) ?: return Error("Member not found.")

        if (!allowsBot && targetMember.isBot)
            return Error("Cannot be a bot")

        return if (authorAsMember.id == targetMember.id) {
            Error("Cannot run command on yourself")
        } else if (authorAsMember.getHighestRolePosition() > targetMember.getHighestRolePosition()) {
            Success(targetMember)
        } else Error("Missing permissions to target this member")
    }
}

private suspend fun Member.getHighestRolePosition() = roles.toList().maxByOrNull { it.rawPosition }?.rawPosition ?: -1
