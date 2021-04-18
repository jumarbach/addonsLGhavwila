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

        if (((IAffectedPlayers) croupier).getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey("werewolf.role.croupier.repeated_target");
            return;
        }
        ((IAffectedPlayers) croupier).addAffectedPlayer(targetWW);

        ((IPower) croupier).setPower(false);

        IPlayerWW pRevealed = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
        allPlayers.remove(pRevealed);
        IRole r1 = pRevealed.getRole();
        IRole r2 = null;
        IRole r3 = null;

        boolean validRoles = false;
        while (!validRoles) {
            IPlayerWW p1 = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
            r2 = p1.getRole();
            allPlayers.remove(p1);
            IPlayerWW p2 = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
            r3 = p2.getRole();
            allPlayers.add(p1);
            if (!(r1.getCamp().equals(r2.getCamp()) && r1.getCamp().equals(r3.getCamp()))) {
                validRoles = true;
            }
        }
        List<String> roles = new ArrayList<>(Arrays.asList(
                r1 instanceof IDisplay ? ((IDisplay) r1).getDisplayRole() : r1.getKey(),
                r2 instanceof IDisplay ? ((IDisplay) r2).getDisplayRole() : r2.getKey(),
                r3 instanceof IDisplay ? ((IDisplay) r3).getDisplayRole() : r3.getKey()));

        Collections.shuffle(roles, game.getRandom());

        allPlayers.add(pRevealed);
        allPlayers.remove(playerWW);
        allPlayers.remove(targetWW);

        IPlayerWW receiver1 = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));
        allPlayers.remove(receiver1);
        //IPlayerWW receiver2 = allPlayers.get((int) Math.floor(game.getRandom().nextFloat() * allPlayers.size()));

        targetWW.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));
        receiver1.sendMessageWithKey("werewolf.role.croupier.card", pRevealed.getName(), game.translate(roles.get(0)),
                game.translate(roles.get(1)), game.translate(roles.get(2)));
        /*if (allPlayers.size() + 2 > 10) {
            receiver2.sendMessageWithKey("werewolf.role.croupier.card", game.translate(pRevealed.getName()), game.translate(roles.get(0)),
                    game.translate(roles.get(1)), game.translate(roles.get(2)));
        }*/
        playerWW.sendMessageWithKey("werewolf.role.croupier.confirm");
    }
}
