package io.havwila.addonsLG.commands;

import fr.ph1lou.werewolfapi.commands.ICommand;
import fr.ph1lou.werewolfapi.enums.Category;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import io.havwila.addonsLG.guess.GuessInventory;
import io.havwila.addonsLG.guess.IGuesser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

        if (targetWW == null || targetWW.isState(StatePlayer.DEATH)) {
            playerWW.sendMessageWithKey("werewolf.check.player_not_found");
            return;
        }

        if (!(playerWW.getRole() instanceof IGuesser)) {
            playerWW.sendMessageWithKey("werewolf.check.permission_denied");
            return;
        }

        IGuesser role = (IGuesser) playerWW.getRole();

        if (!role.canGuess(targetWW)) return;

        Set<Category> categories = role.getAvailableCategories();
        if (categories == null || categories.isEmpty()) {
            categories = new HashSet<>();
            categories.add(Category.VILLAGER);
            categories.add(Category.NEUTRAL);
            categories.add(Category.WEREWOLF);
            categories.add(Category.ADDONS);
        }

        GuessInventory.getInventory(targetWW, categories).open(player);
    }
}
