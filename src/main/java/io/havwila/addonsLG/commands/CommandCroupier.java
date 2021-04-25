package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.*;
import io.havwila.addonsLG.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandCroupier implements ICommands {

    private final Main main;

    public CommandCroupier(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getAPI().getWereWolfAPI();

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) {
            return;
        }

        List<IPlayerWW> allPlayers = game.getPlayerWW().stream().filter(p -> p.isState(StatePlayer.ALIVE)).collect(Collectors.toList());

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
        IPlayerWW targetWW = game.getPlayerWW(argUUID);

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

        IPlayerWW pRevealed = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
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

        if (enemyPlayers.isEmpty()) {
            IPlayerWW p = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
            allPlayers.remove(p);
            r2 = p.getRole();
        } else {
            IPlayerWW p = enemyPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
            allPlayers.remove(p);
            r2 = p.getRole();
        }
        r3 = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size())).getRole();

        List<String> roles = new ArrayList<>(Arrays.asList(
                r1 instanceof IDisplay ? ((IDisplay) r1).getDisplayRole() : r1.getKey(),
                r2 instanceof IDisplay ? ((IDisplay) r2).getDisplayRole() : r2.getKey(),
                r3 instanceof IDisplay ? ((IDisplay) r3).getDisplayRole() : r3.getKey()));

        Collections.shuffle(roles, game.getRandom());

        allPlayers.add(pRevealed);
        allPlayers.remove(playerWW);
        allPlayers.remove(targetWW);

        IPlayerWW receiver = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));

        targetWW.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));
        receiver.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));

        playerWW.sendMessageWithKey("werewolf.role.croupier.confirm");
    }
}
