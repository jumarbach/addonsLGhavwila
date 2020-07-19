package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.FirstDeathEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesVillage;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class Medium extends RolesVillage{

    public Medium(GetWereWolfAPI main, WereWolfAPI game, UUID uuid) {
        super(main,game,uuid);
    }

    @Override
    public String getDescription() {
        return game.translate("werewolf.role.medium.description");
    }

    @Override
    public String getDisplay() {
        return "werewolf.role.medium.display";
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event){

        PlayerWW p = game.getPlayersWW().get(event.getUuid());
        if(Bukkit.getPlayer(getPlayerUUID()) != null){
            TextComponent medium_msg = new TextComponent(game.translate("werewolf.role.medium.death_message", p.getName()));
            Bukkit.getPlayer(getPlayerUUID()).spigot().sendMessage(medium_msg);
        }
    }
}
