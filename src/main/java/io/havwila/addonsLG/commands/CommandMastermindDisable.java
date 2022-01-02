package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.havwila.addonsLG.roles.Inquisitor;
import io.havwila.addonsLG.roles.Mastermind;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandMastermindDisable implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }
        Mastermind mastermind = (Mastermind) playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey("werewolf.check.offline_player");
        }

        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW targetWW = game.getPlayerWW(argUUID).orElse(null);

        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!mastermind.getAffectedPlayers().contains(targetWW)) {
            playerWW.sendMessageWithKey("havwila.role.mastermind.not_guessed", Formatter.format("&player&", targetWW));
            return;
        }

        playerWW.removePlayerMaxHealth(2);
        targetWW.getRole().disableAbilities();
        playerWW.sendMessageWithKey("havwila.role.mastermind.disable_perform",
                Formatter.format("&player&", targetWW.getName()));
        targetWW.sendMessageWithKey("havwila.role.mastermind.disable_target");
    }
}
