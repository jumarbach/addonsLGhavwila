package io.havwila.addonsLG.commands;

import fr.ph1lou.werewolfapi.annotations.RoleCommand;
import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.commands.ICommandRole;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import io.havwila.addonsLG.roles.Inquisitor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RoleCommand(key = "havwila.role.inquisitor.command", roleKeys = {"havwila.role.inquisitor.display"},
        argNumbers = 1,
        requiredPower = true)
public class CommandInquisitor implements ICommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {

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
