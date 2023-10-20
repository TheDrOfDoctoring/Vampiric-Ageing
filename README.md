# Vampiric Ageing

### This is a highly configurable addon for Vampirism that adds an Ageing Mechanic for Vampires and if installed, Werewolves.

## Vampires

By default, Ageing begins at Vampire Level 14 and to increase their Age Rank the player must drain a significant amount of blood, the progress of which can be checked by interacting with a Coffin. The Age Rank can be seen above the player's blood bar.

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
- Enhanced Regeneration, scaling with Age Rank (Disabled by default)

However, some penalties also come with increasing age:

- Increased vulnerability to Fire, Garlic and Holy Water scaling with Age Rank
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

## Hunters

By default, Ageing begins at Hunter Level 14 (Some configuration options are the same as the main config file). To increase their Age Rank, a player must hunt enemy faction creatures such as Vampires or Werewolves. Different creatures contribute different amount of points towards progressing to the next Age Rank. This progress can be checked by interacting with an Injection Chair.

### Abilities

As Hunters age, they learn to use the other factions abilities against them, this allows them to gain these abilities with ageing:

- Increased damage against enemy faction creatures, scaling with age rank.
- Increased health, scaling with age rank
- Increased movement speed, scaling with age rank
- Step Assist at Age 4
- Faster regeneration at Age 3
- New "Seniority" Oil that does more damage to werewolves or vampires the higher their age.

However, there are some penalties upon ageing.

- Faster exhaustion (Food Drain), scaling with age rank
- Decreased XP gain, scaling with age rank

The most powerful abilities is restricted to tainting a hunters blood, however..

### Tainted Blood
By default, at Age Rank 2 a hunter gains the ability to make Tainted Blood. This can be drunk to give the hunter vampiric abilities based on their cumulative age rank (Tainted Blood Age + Hunter Age Rank)

Tainted Blood can be cleared using a Garlic Injection.

These are the abilities for the different cumulative age ranks. Each rank also includes previous ranks, for example 8-9 will also include the increased max health from 6-7, and 10 will include teleport from 8-9. 

Most abilities will scale will higher cumulative ranks.
#### 0 - 2
- No difference, impossible on default configs.

#### 3 - 5
- Increased mining speed, further scales with cumulative rank
- Increased attack damage, scales with cumulative rank
- Fire now does more damage, scales with cumulative rank

#### 6 - 7 


- Increased max health, further scaling with cumulative rank
- Holy water now deals damage to the hunter

#### 8 - 9

- Teleport Action
- Increased movement speed, further scaling with cumulative rank
- Worse trade deals with villagers, further scaling with cumulative rank
- Sun begins to affect hunter slowly

#### 10 

- Sun affect hunters significantly faster
- Limited Bat Mode

Additionally, a hunter can undergo a permanent transformation which allows them to permanently have a Tainted Age of 6. This allows them to go to Cumulative Age 11 at max Age Rank

#### 11

- Infinite duration bat mode
- All previous benefits further improved
- Infinite breath underwater

- Harsher weaknesses

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
- Raw Meat is even more nourishing based on age rank

However, there are some penalties upon ageing.
- Increased damage from silver oil (Optional, now disabled by default)
- Increased damage from hunter
- Harsher prices with villagers (same config option as for vampires)