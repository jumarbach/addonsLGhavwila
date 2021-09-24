package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.havwila.addonsLG.guess.GuessInventory;
import io.havwila.addonsLG.guess.IGuesser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGuess implements ICommand {

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

        if (targetWW == null) {
            return;
        }

        if (!(playerWW.getRole() instanceof IGuesser)) {
            playerWW.sendMessageWithKey("werewolf.check.permission_denied");
            return;
        }

        IGuesser role = (IGuesser) playerWW.getRole();

        if (!role.canGuess(targetWW)) return;

        GuessInventory.getInventory(targetWW).open(player);
    }
}
