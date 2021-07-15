package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.havwila.addonsLG.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandCroupier implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        List<IPlayerWW> allPlayers = game.getPlayersWW().stream().filter(p -> p.isState(StatePlayer.ALIVE)).collect(Collectors.toList());

        if (allPlayers.size() < 5) {
            playerWW.sendMessageWithKey("werewolf.role.croupier.not_enough_players");
            return;
        }

        IRole croupier = playerWW.getRole();
        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || !targetWW.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (targetWW == playerWW) {
            playerWW.sendMessageWithKey("werewolf.role.croupier.yourself");
            return;
        }

        List<IPlayerWW> targetedPlayers = new ArrayList<>();
        List<IPlayerWW> exposedPlayers = new ArrayList<>();

        ListIterator<? extends IPlayerWW> affectedPlayers = ((IAffectedPlayers) croupier).getAffectedPlayers().listIterator();
        while (affectedPlayers.hasNext()) {
            targetedPlayers.add(affectedPlayers.next());
            if (!affectedPlayers.hasNext()) {
                ((IPower) croupier).setPower(false);
                playerWW.sendMessageWithKey("werewolf.role.croupier.all_exposed");
                return;
            }
            IPlayerWW p = affectedPlayers.next();
            if (p.isState(StatePlayer.ALIVE)) {
                exposedPlayers.add(p);
            }
        }

        if (targetedPlayers.contains(targetWW)) {
            playerWW.sendMessageWithKey("werewolf.role.croupier.repeated_target");
            return;
        }
        ((IAffectedPlayers) croupier).addAffectedPlayer(targetWW);

        ((IPower) croupier).setPower(false);

        allPlayers.removeAll(exposedPlayers);
        if (allPlayers.isEmpty()) {
            playerWW.sendMessageWithKey("werewolf.role.croupier.all_exposed");
            return;
        }

        IPlayerWW pRevealed = allPlayers.get(game.getRandom().nextInt(allPlayers.size()));
        allPlayers.remove(pRevealed);
        ((IAffectedPlayers) croupier).addAffectedPlayer(pRevealed);
        allPlayers.addAll(exposedPlayers);

        IRole r1 = pRevealed.getRole();
        IRole r2 = null;
        IRole r3 = null;

        List<IPlayerWW> enemyPlayers = new ArrayList<>();
        ListIterator<IPlayerWW> iterator = allPlayers.listIterator();
        while (iterator.hasNext()) {
            IPlayerWW p = iterator.next();
            if (!p.getRole().getCamp().equals(pRevealed.getRole().getCamp())) {
                enemyPlayers.add(p);
            }
        }

        IPlayerWW p;
        if (enemyPlayers.isEmpty()) {
            p = allPlayers.get(game.getRandom().nextInt(allPlayers.size()));
        } else {
            p = enemyPlayers.get(game.getRandom().nextInt(enemyPlayers.size()));
        }
        allPlayers.remove(p);
        r2 = p.getRole();
        r3 = allPlayers.get(game.getRandom().nextInt(allPlayers.size())).getRole();

        List<String> roles = new ArrayList<>(Arrays.asList(
                r1.getDisplayRole(),
                r2.getDisplayRole(),
                r3.getDisplayRole()));

        Collections.shuffle(roles, game.getRandom());

        allPlayers.add(pRevealed);
        allPlayers.remove(playerWW);
        allPlayers.remove(targetWW);

        IPlayerWW receiver = allPlayers.get(game.getRandom().nextInt(allPlayers.size()));

        targetWW.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));
        receiver.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));

        playerWW.sendMessageWithKey("werewolf.role.croupier.confirm");
    }
}
