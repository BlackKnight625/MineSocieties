# Introduction

This project is being done for my Master's thesis, titled "Minecraft Social Agents".

The goal is to create socially intelligent and player-looking NPCs. An NPC is socially intelligent if it can have social interactions with players and with other NPCs.

Usually, social NPCs in games such as The Legend of Zelda are very limited. Their dialog is hard-coded and they always say the same things. I intend to create NPCs that choose actions based on the context that they find themselves in: their current state of mind, personality, past actions, their memory, recent events, etc. This way, NPCs could refuse talking to a player if said player was rude or punched them.
 
You may read many more details regarding my goals by looking at my Thesis Proposal inside of the "Thesis" folder.

You may see the NPCs in action in this video: https://www.youtube.com/watch?v=3SSp0QyYRa8

# Installation
WIP

# In-game usage
## Deploying an NPC

```/agent deploy "<name>" <initial description>```

Example:
```/agent deploy "Steve Smith" Steve is a software engineer. He loves chocolate.```

Note: Do not forget the quotes around the name, as it allows the plugin to distinguish between the NPC's name and its description.

## Allowing NPCs to choose actions
Since most Large Language Models have a pay-per-use model, the plugin does not allow NPCs to choose actions by default. You must enable this feature by setting the option 'agentsCanStartChoosingActionsByDefault' in the config.yml file to true, or by typing the following command:

```/agent allow_action_choice true```

And to later on disable it:

```/agent allow_action_choice false```

## Talking with NPCs
To talk with an NPC, you must be close (range defined in the config) to it and type the following command:

```/agent talk "<name>" <message>```

or 

```/agent talk <name> <message>```

if the NPC has a single-word name.

Example:

```/agent talk "Steve Smith" Hello! How was your day?```

The NPC will talk back to you or choose a different action if allow_action_choice is set to true.

## Editing NPCs

Editing NPCs is done through an in-game GUI. To open it, you must go near an NPC and click on them while holding the NPC Edit Stick, obtained with:

```/agent edit_stick```

The GUI allows you to change the NPC's name, memories, force them to choose an action, and more. You can also configure locations that the NPC can know about so that may choose to go to them. In order to do this without needing to click an NPC, you can run the following command:

```/agent locations```

## Backup NPCs
If you want to preserve the current state of all NPCs and configured locations, run:

```/agent backup save <save name>```

In order to restore a previously saved backup, run:

```/agent backup load <save name>```

**Note:** a backup load will replace all the NPCs in the world with the ones saved in the backup, effectively deleting them. If you want to preserve them, then run the backup save command and then run the backup load command.
