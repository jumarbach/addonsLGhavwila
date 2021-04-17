package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;

import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.EventHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class Medium extends RoleVillage{

    public Medium(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.medium.description");
    }

    @Override
    public void recoverPower() {

    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event){

        IPlayerWW p = event.getPlayerWW();
        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            TextComponent medium_msg = new TextComponent(game.translate("werewolf.role.medium.death_message", p.getName()));
            Bukkit.getPlayer(getPlayerUUID()).spigot().sendMessage(medium_msg);
        }
    }
}
