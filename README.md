# addonsLGhavwila
addon for Ph1lou's Werewolf plugin: https://github.com/Ph1Lou

depends on https://github.com/Ph1Lou/WereWolfAPI

If you'd like an english translation, please contact me.

Falls eine Deutsche übersetzung erwünscht ist, kontaktiert mich bitte.

Si vous souhaitez réutiliser/réimplémanter les roles sur votre propre plugin LG, pas de soucis. Je vous prierais juste de me laisser un petit message dans ce cas.

## Description

Ajoute les roles suivants:

### Inquisiteur

Villageois, aura neutre

Chaque jour, il peut désactiver les capacités d'un loup se son choix pour le restant de la game. 
Si il cible un joueur qui n'est pas loup, il perd son pouvoir pour le reste de la game.

### Auramancien

Villageois, aura neutre

Dispose de pouvoirs différents en fonction de son aura.
Lorsque il est affecté par un modificateur d'aura, son aura devient l'aura du modificateur. Lorsque il tue un joueur avec une aura sombre, son aura s'améliore d'un cran (Sombre->Neutre->Lumineuse). S'il tue un joueur avec une autre aura, son aura devient Sombre.

##### Pouvoirs
* Lumineux: Les joueurs proches avec une aura Lumineuse disposent des 0.5 force de dégats supplémentaires. Au début du jour, retire aux joueurs proches le modificateur d'aura résultant du kill d'un joueur.
* Neutre: Lorsque un joueur est tué proche de lui, il est informé des auras du tueur et du tué
* Sombre: Doit gagner seul et inflige des dégats de force aux joueurs lumineux. Dispose du pseudo d'un joueur à l'aura Lumineuse et son aura ne change plus s'il ne reste plus de joueurs à l'aura lumineuse

### Témoin

Villageois, aura lumineuse

Le témoin apprends le pseudo d'un loup lors de l'annonce de la liste des loups. Si ce loup venait a mourrir, le témoin perd 4 coeurs permanent. De plus, il inflige 30% de dégats en moins au loup qu'il connait.

### Croupier

Villageois, aura neutre

Chaque jour, il peut choisir un joueur qui obtiendra le pseudo d'un joueur aléatoire ainsi que 3 roles (dont au moins 2 de camps différents si possible), parmis lesquels se trouve le role du joueur aléatoire. Une option pour qu'il n'utilise son pouvoir que tous les deux jours est disponible.

### Chasseur

Villageois, aura neutre

Lorsque il reste 1/2/3/4 minutes à moins de 20 blocs de l'endroit où un joueur est mort, il obtient des informations sur le mort, son rôle et les joueurs présents dans un rayon de 100 blocs au moment de la mort.

Il dispose d'un arc, 64 flèches et un livre power 3.

### Romulus ou Remus

Neutre, aura neutre, camp de façade Villageois

Au début, 2 frères qui se connaissant dès l'annonce des la liste des loups, qui ont une flèche indiquant la position de l'autre et on résistance quand ils sont éloignés de plus de 60 blocs. Ils connaissant aussi le rôle exacte de leur mère adoptive, un loup. S'ils passent à coté de leur mère adoptive, celle-ci recevra 2 minutes plus tard un message lui indiquant qu'un des ses fils est passé à coté d'elle. Ils peuvent gagner en famille à trois.

Si la mère meurt, les deux frères apprennent s'ils sont Romulus ou Remus. Romulus est villageois, Remus est loup, mais sans apparaitre dans la liste ou la connaitre. S'ils parviennent à tuer l'autre frère, ils obtiennent un effet de force permanent. Remus obtient en plus de ça la liste des loups et apparait dans celle-ci. Si la mère est morte de la main de Remus, celui-ci doit gagner seul tel un LGB.

Si on des deux frères meurt avant la mère, l'autre aura à gagner seul sans aucun pouvoir.

### Loup Muselant

Loup, aura Sombre

Chaque nuit, peut essayer de deviner le role d'un villageois. Si il réussit, le joueur deviné perd ses pouvoirs. S'il échoue, le loup muselant perd ses pouvoirs.

### Mastermind

Neutre, aura Neutre

Peut deviner le role d'autres joueurs. S'il réussit à deviner le role d'un joueur il gagne un coeur permanent, s'il échoue il perd un coeur permanent.

Il peut utiliser la command /ww mindsilence \<pseudo\> pour payer un coeur permanent et désactiver les pouvoirs d'un joueur dont il a deviné le role.

Il peut utiliser la commande /ww mindswap \<pseudo\> \<pseudo\> pour payer deux coeurs permanents et échanger les roles de deux joueurs dont il a deviné les roles.

## Changelog

2.2.0: Update API

2.1.1 Retrait Chasseur

2.1.0: Update API

2.0.1: Bugfix Mastermind et command deviner

2.0.0: Ajout Mastermind et Loup Muselant, rework Auramancien

1.2.2: Ajout config permettant au chasseur de tirer et retrait du code du medium pour des raisons de compatibilité

1.2.1: Ajout Inquisiteur et Auramancien (Compatible avec la 1.8 une fois que la snapshot sort)

1.2.0: Ajout Romulus Ou Remus

1.1.6: Bugfix Croupier, LTS release 1.1.7 du plugin principal

1.1.5: Update API

1.1.4: Update API, compatible avec l'oracle

1.1.3: Retrait Medium, remplacé par le nouveau Chaman

1.1.2: Ajout Chasseur, correction de bugs

1.1.1: Correction de bugs

1.1.0: Ajout Croupier


