package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.havwila.addonsLG.roles.Inquisitor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHunter implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

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

        if (playerWW.getLastKiller().isPresent() && playerWW.getLastKiller().get().equals(targetWW)) {
            playerWW.sendMessageWithKey("havwila.role.hunter.invalid_target");
            return;
        }
        ((IPower) playerWW.getRole()).setPower(false);

        targetWW.removePlayerHealth(10);
        Bukkit.broadcastMessage(game.translate("havwila.role.hunter.success", Formatter.format("&target&", targetWW.getName())));

    }
}
