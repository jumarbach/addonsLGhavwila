# addonsLGhavwila
addon for Ph1lou's Werewolf plugin: https://github.com/Ph1Lou

depends on https://github.com/Ph1Lou/WereWolfAPI

## Description

Ajoute les roles suivants:

### Témoin

Villageois.

Le témoin apprends le pseudo d'un loup lors de l'annonce de la liste des loups. Si ce loup venait a mourrir, le témoin perd 4 coeurs permanent

### Croupier

Villageois.

Chaque jour, il peut choisir un joueur qui obtiendra le pseudo d'un joueur aléatoire ainsi que 3 roles (dont au moins 2 de camps différents si possible), parmis lesquels se trouve le role du joueur aléatoire. Une option pour qu'il n'utilise son pouvoir que tous les deux jours est disponible.

### Chasseur

Villageois.

Lorsque il reste 1/2/3/4 minutes à moins de 20 blocs de l'endroit où un joueur est mort, il obtient des informations sur le mort, son rôle et les joueurs présents dans un rayon de 100 blocs au moment de la mort.

Il dispose d'un arc, 64 flèches et un livre power 3.

### Medium (supprimé)

Villageois.

Le medium est informé de la mort d'un joueur instantanément. Etant donné que cela lui permet de savoir quand quelqu'un ressucite, il est conseillé de l'utiliser dans des compositions où l'infection est un bonus et non une nécéssité pour les loups.

Le medium a été remplacé par le nouveau Chaman dans le plugin principal. Si vous souhaitez tout de même utiliser le medium, il vous faut retirer les commenentaires qui retirent l'enregistrement du medium dans Main.java et ensuite compiler vous-même le code.

## Changelog

1.1.3: Retrait Medium, remplacé par le nouveau Chaman

1.1.2: Ajout Chasseur, correction de bugs

1.1.1: Correction de bugs

1.1.0: Ajout Croupier


