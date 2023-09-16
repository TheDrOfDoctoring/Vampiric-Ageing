# Vampiric Ageing

### This is a highly configurable addon for Vampirism that adds an Ageing Mechanic for Vampires and if installed, Werewolves.

## Vampires

By default, Ageing begins at Vampire Level 14 and to increase their Age Rank the player must infect a number of entities, the progress of which can be checked by interacting with a Coffin. The Age Rank can be seen above the player's blood bar.

Ageing goes up to a max rank of 5 with each rank further increasing the player's abilities, unlocking new ones, and potentially harshening the existing vulnerabilities of the vampire. 

### Abilities

By default, the player gains access to a number of benefits from ranking up:

- Increased health, scaling with Age Rank
- A reduction in Sun Damage, scaling with Age Rank
- Less Blood Drain, scaling with Age Rank
- Step Assist at Rank 2
- A new Action at Rank 3, which while active allows the player to drain blood from creatures they attack
- Waterwalking at Rank 4
- Vampires are now mostly unaffected by Powdered Snow
- Celerity (Speed Increase) Action at Rank 1
- Increased Damage, scaling with Age Rank
- Enhanced Regeneration, scaling with Age Rank

However, some penalties also come with increasing age:

- Increased vulnerability to Fire, Garlic and Holy Water scaling with Age Rank
- Rare chance to randomly gain the Bad Omen effect 
- Harsher prices with villagers
- Hunter Mobs deal more damage

### Sire Mechanic

Siring is an optional config that changes how Ageing works, when using this it is highly recommended to turn off Age Reset and to turn on Advanced Vampire Age (Enabled by Default) and to turn off the other forms of Ageing.

With this mechanic enabled, when a player has a Glass Bottle in their off-hand and kills a Vampire with an Age Rank (Only players and advanced vampires can have an age rank), they gain a blood bottle with that rank. This bottle can be drunk to get that rank.

A player can also gain a rank when they become a vampire if they were infected by an Advanced Vampire with an Age Rank above 1.

Additionally, players with an Age Rank can use a Glass Bottle while Shifting to make a Blood Bottle with the rank below theirs, e.g if a Player has Rank 5, they will produce a bottle with Rank 4.

### Additional Configuration Options

Everything previously mentioned has configuration options for if its enabled and depending on the feature more detailed configs, for example the reduction in sun damage can be completely inverted to increase sun damage.

The mod also comes with some more debatable design choices disabled by default such as the Sire Mechanic, but there is also options for using a time based or drain based system of ranking up, or greatly increasing the damage dealt to a vampire when they are starving to death.

All of these options can be found in ``/config/vampiricageing-common.toml``, I recommend you take a look!

## Werewolves


By default, Ageing begins at Werewolf Level 14 (Some configuration options are the same as the main config file). To increase their Age Rank, a player must kill creatures using their Bite Attack. Different creatures contribute different amount of points towards progressing to the next Age Rank. This progress can be checked by interacting with a Stone Altar's Fire Bowl.

### Abilities

By default, the player gains access to a number of benefits from ranking up:

- Increased bite damage, scaling with age rank
- Increased health, scaling with age rank
- Increased damage, scaling with age rank
- Wolves summoned from howling are stronger based on age
- Heal on bite based on age
- Biting mobs nourishes you
- Can stay in werewolf forms longer

However, there are some penalties upon ageing.
- Increased damage from silver oil (Optional, now disabled by default)
- Increased damage from hunter
- Harsher prices with villagers (same config option as for vampires)