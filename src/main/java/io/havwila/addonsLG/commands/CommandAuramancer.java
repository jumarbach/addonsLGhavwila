package io.havwila.addonsLG.commands;

import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.havwila.addonsLG.roles.Auramancer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.text.Normalizer;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandAuramancer implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) {
            return;
        }

        Auramancer auramancer = (Auramancer) playerWW.getRole();
        Aura mode = auramancer.getAura();

        Location location = playerWW.getLocation();

        Set<IPlayerWW> nearbyPlayers = game.getPlayersWW()
                .stream()
                .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                .filter(playerWW2 -> !playerWW2.equals(playerWW))
                .filter(playerWW2 -> playerWW2.getLocation().distance(location) < 50)
                .collect(Collectors.toSet());

        if (nearbyPlayers.size() < 2) {
            playerWW.sendMessageWithKey("werewolf.role.auramancer.not_enough_players");
            return;
        }
        auramancer.setPower(false);

        switch (mode) {
            case LIGHT:
                nearbyPlayers.forEach(nearbyWW -> nearbyWW.getRole().removeAuraModifier("killer"));

                nearbyPlayers.removeIf(nearbyWW -> !nearbyWW.getRole().getAura().equals(Aura.LIGHT));
                nearbyPlayers.forEach(nearbyWW -> {
                    nearbyWW.addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, 2400, 0, "auramancer"));
                    nearbyWW.sendMessageWithKey("werewolf.role.auromancer.command_light_others");
                    playerWW.sendMessageWithKey("werewolf.role.auromancer.command_light_self",
                            Formatter.format("&aura&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
                });
                break;
            case NEUTRAL:
                Set<IPlayerWW> dark = nearbyPlayers.stream().filter(nearbyWW -> nearbyWW.getRole().getAura().equals(Aura.DARK)).collect(Collectors.toSet());
                Set<IPlayerWW> neutral = nearbyPlayers.stream().filter(nearbyWW -> nearbyWW.getRole().getAura().equals(Aura.NEUTRAL)).collect(Collectors.toSet());
                Set<IPlayerWW> light = nearbyPlayers.stream().filter(nearbyWW -> nearbyWW.getRole().getAura().equals(Aura.LIGHT)).collect(Collectors.toSet());
                playerWW.sendMessageWithKey("werewolf.role.auramancer.command_neutral",
                        Formatter.format("&light_p&", Integer.toString(light.size())),
                        Formatter.format("&light_a&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                        Formatter.format("&neutral_p&", Integer.toString(neutral.size())),
                        Formatter.format("&neutral_a&", Aura.NEUTRAL.getChatColor() + game.translate(Aura.NEUTRAL.getKey())),
                        Formatter.format("&dark_p&", Integer.toString(dark.size())),
                        Formatter.format("&dark_a&", Aura.DARK.getChatColor() + game.translate(Aura.DARK.getKey())));
                neutral.forEach(nearbyWW -> nearbyWW.getRole().addAuraModifier(new AuraModifier("auramancer", Aura.LIGHT, -1, false)));
                break;
            case DARK:
                nearbyPlayers.forEach(nearbyWW -> {
                    nearbyWW.getRole().removeAuraModifier("killer");
                    if (nearbyWW.getRole().getAura().equals(Aura.NEUTRAL)) {
                        nearbyWW.getRole().addAuraModifier(new AuraModifier("auramancer", Aura.LIGHT, 15, false));
                    }
                    if (nearbyWW.getRole().getAura().equals(Aura.LIGHT)) {
                        nearbyWW.addPotionModifier(PotionModifier.add(PotionEffectType.SLOW, 2400, 0, "auramancer"));
                        nearbyWW.sendMessageWithKey("werewolf.role.auramancer.command_dark_others");
                    }
                    playerWW.sendMessageWithKey("werewolf.role.auramancer.command_dark_self",
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
                });
        }
    }
}
