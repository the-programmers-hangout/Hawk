# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| [Argument]  | Argument is not required.      |
| Argument... | Accepts many of this argument. |

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

## Party
| Commands           | Arguments                          | Description                                     |
| ------------------ | ---------------------------------- | ----------------------------------------------- |
| getPartySymbol     |                                    | View the current party mode symbol              |
| partyChannelFilter | PartyChannelOption, [PartyChannel] | Add, remove or view channels used in party mode |
| setPartySymbol     | Suffix                             | Set new party mode symbol                       |
| toggleParty        |                                    | Toggles party mode                              |

## ReactionRole
| Commands           | Arguments               | Description                  |
| ------------------ | ----------------------- | ---------------------------- |
| createReactionRole | Roles, EmbedDescription | Create a reaction role embed |

## Utility
| Commands | Arguments     | Description               |
| -------- | ------------- | ------------------------- |
| help     | [CommandName] | Display help information. |

