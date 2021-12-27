package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.havwila.addonsLG.roles.Inquisitor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandInquisitor implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }
        Inquisitor inquisitor = (Inquisitor) playerWW.getRole();

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


        inquisitor.addAffectedPlayer(targetWW);

        if (targetWW.getRole().isWereWolf()) {
            targetWW.getRole().disableAbilities();

            targetWW.sendMessageWithKey("havwila.role.inquisitor.smite_disable");
            playerWW.sendMessageWithKey("havwila.role.inquisitor.smite_success");
        } else {
            inquisitor.disableAbilities();
            playerWW.sendMessageWithKey("havwila.role.inquisitor.smite_fail");
        }
    }
}
