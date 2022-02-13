# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| [Argument]  | Argument is not required.      |

## Configuration
| Commands      | Arguments              | Description                          |
| ------------- | ---------------------- | ------------------------------------ |
| configuration |                        | View the bot configuration           |
| setChannel    | ChannelChoice, Channel | Set the logging or alert channel     |
| setPrefix     | Text                   | Set the bot prefix.                  |
| setRole       | RoleChoice, RoleName   | Set the bot staff or admin role.     |
| setup         |                        | Setup a configuration for this guild |

## Nickname
| Commands  | Arguments                  | Description                           |
| --------- | -------------------------- | ------------------------------------- |
| blocklist | BloclklistOption, [symbol] | Add a symbol to the symbol blocklist. |
| nick      | LowerMemberArg, Nickname   | Set a member's nickname               |

## Party
| Commands           | Arguments                     | Description                                     |
| ------------------ | ----------------------------- | ----------------------------------------------- |
| getPartySymbol     |                               | View the current party mode symbol              |
| partyChannelFilter | PartyChannelOption, [Channel] | Add, remove or view channels used in party mode |
| setPartySymbol     | Suffix                        | Set new party mode symbol                       |
| toggleParty        |                               | Toggles party mode                              |

## Utility
| Commands | Arguments | Description               |
| -------- | --------- | ------------------------- |
| help     | [Command] | Display help information. |

