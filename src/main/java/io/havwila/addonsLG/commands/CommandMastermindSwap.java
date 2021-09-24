package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.random_events.SwapEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.havwila.addonsLG.roles.Mastermind;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandMastermindSwap implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }
        Mastermind mastermind = (Mastermind) playerWW.getRole();

        Player playerArg1 = Bukkit.getPlayer(args[0]);

        if (playerArg1 == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
        }

        UUID argUUID1 = playerArg1.getUniqueId();
        IPlayerWW targetWW1 = game.getPlayerWW(argUUID1).orElse(null);

        if (targetWW1 == null || !targetWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!mastermind.getAffectedPlayers().contains(targetWW1)) {
            playerWW.sendMessageWithKey("werewolf.role.mastermind.not_guessed", Formatter.format("&player&", targetWW1.getName()));
            return;
        }


        Player playerArg2 = Bukkit.getPlayer(args[1]);

        if (playerArg2 == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
        }

        UUID argUUID2 = playerArg2.getUniqueId();
        IPlayerWW targetWW2 = game.getPlayerWW(argUUID2).orElse(null);

        if (targetWW2 == null || !targetWW2.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!mastermind.getAffectedPlayers().contains(targetWW2)) {
            playerWW.sendMessageWithKey("werewolf.role.mastermind.not_guessed", Formatter.format("&player&", targetWW2.getName()));
            return;
        }

        mastermind.setPower(false);

        SwapEvent swapEvent = new SwapEvent(targetWW1, targetWW2);
        Bukkit.getPluginManager().callEvent(swapEvent);

        if (swapEvent.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }
        playerWW.removePlayerMaxHealth(4);

        IRole roles1 = targetWW1.getRole();
        IRole roles2 = targetWW2.getRole();
        targetWW1.setRole(roles2);
        targetWW2.setRole(roles1);
        targetWW1.addPlayerMaxHealth(20 - targetWW1.getMaxHealth());
        targetWW2.addPlayerMaxHealth(20 - targetWW2.getMaxHealth());
        targetWW1.clearPotionEffects();
        targetWW2.clearPotionEffects();
        targetWW1.sendMessageWithKey("werewolf.random_events.swap.concerned");
        targetWW2.sendMessageWithKey("werewolf.random_events.swap.concerned");
        playerWW.sendMessageWithKey("werewolf.role.mastermind.swap_perform");
        roles1.recoverPower();
        roles2.recoverPower();
        roles1.recoverPotionEffects();
        roles2.recoverPotionEffects();


    }
}
