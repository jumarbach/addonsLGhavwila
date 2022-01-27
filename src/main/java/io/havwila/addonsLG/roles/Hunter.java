package io.havwila.addonsLG.roles;

import fr.minuskube.inv.ClickableItem;
import fr.ph1lou.werewolfapi.game.IConfiguration;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.interfaces.IPower;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.utils.BukkitUtils;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Hunter extends RoleVillage implements IAffectedPlayers, IPower {

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();
    private final List<HunterClue> clues = new ArrayList<>();
    private int secondsCount = 0;
    boolean power = false;

    public Hunter(WereWolfAPI game, IPlayerWW playerWW, String key) {
        super(game, playerWW, key);
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }

    @Override
    public @NotNull String getDescription() {
        DescriptionBuilder descBuilder = new DescriptionBuilder(game, this)
                .setDescription(game.translate("havwila.role.hunter.description"))
                .setItems(game.translate("havwila.role.hunter.items"));
        if (game.getConfig().isConfigActive("havwila.role.hunter.can_shoot")) {
            descBuilder = descBuilder.addExtraLines(game.translate("havwila.role.hunter.description_shoot"));
        }
        return descBuilder.build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler
    public void onFinalDeathEvent(FinalDeathEvent event) {

        IPlayerWW playerWW = event.getPlayerWW();

        if (playerWW.equals(this.getPlayerWW())) {
            if (game.getConfig().isConfigActive("havwila.role.hunter.can_shoot")) {
                this.setPower(true);
                getPlayerWW().sendMessageWithKey("havwila.role.hunter.perform");
                BukkitUtils.scheduleSyncDelayedTask(() -> {
                    getPlayerWW().sendMessageWithKey("werewolf.check.end_selection");
                    setPower(false);
                }, 20 * 30);
            }
            return;
        }

        if (getPlayerWW().isState(StatePlayer.DEATH)) {
            return;
        }

        Location deathLocation = playerWW.isState(StatePlayer.ALIVE) ? playerWW.getLocation() : playerWW.getSpawn();
        Set<IPlayerWW> nearbyPlayers = Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> {
                    try {
                        return deathLocation.distance(player.getLocation()) < 70;
                    } catch (Exception ignored) {
                        return false;
                    }
                })
                .map(Entity::getUniqueId)
                .map(game::getPlayerWW)
                .filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(player -> player.isState(StatePlayer.ALIVE))
                .filter(player -> !player.equals(playerWW))
                .collect(Collectors.toSet());

        clues.add(new HunterClue(playerWW, deathLocation, nearbyPlayers));
    }

    @Override
    public void second() {
        if (!(++secondsCount % 10 == 0)) {
            return;
        }
        secondsCount = 0;
        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!isAbilityEnabled()) return;

        Location location = getPlayerWW().getLocation();
        double distanceOrigin = location.length();
        clues.forEach(clue -> {
            //double comparisons are faster than Location comparisons
            if (clue.getDistanceToOrigin() + 20 < distanceOrigin || clue.getDistanceToOrigin() - 20 > distanceOrigin) {
                return;
            }
            if (clue.getDeathLocation().distance(location) > 20) {
                return;
            }
            clue.incrementCount();
            switch (clue.getCount()) {
                case 6:
                    affectedPlayers.add(clue.getPlayerWW());
                    getPlayerWW().sendMessageWithKey("havwila.role.hunter.clue_player", Formatter.format("&player&", clue.getPlayerWW().getName()),
                            Formatter.format("&number&", Integer.toString(clue.getNearbyPlayers().size())));
                    return;
                case 12:
                    getPlayerWW().sendMessageWithKey("havwila.role.hunter.clue_role", Formatter.format("&victim&", clue.getPlayerWW().getName()),
                            Formatter.format("&role&", game.translate(clue.getPlayerWW().getRole().getKey())),
                            Formatter.format("&players&", buildNamesString(clue.getNamesList(), 1)));
                    return;
                case 18:
                    getPlayerWW().sendMessageWithKey("havwila.role.hunter.clue_nearby", Formatter.format("&players&",  buildNamesString(clue.getNamesList(), 2)),
                            Formatter.format("&victim&", clue.getPlayerWW().getName()));
                    return;
                case 24:
                    getPlayerWW().sendMessageWithKey("havwila.role.hunter.clue_nearby", Formatter.format("&players&", buildNamesString(clue.getNamesList(), 3)),
                            Formatter.format("&victim&", clue.getPlayerWW().getName()));
                    clues.remove(clue);
                    return;
                default:
                    return;
            }

        });
    }

    private String buildNamesString(List<String> names, int thirds) {
        int nReadable = names.size() * thirds / 3;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (i < nReadable) {
                list.add(names.get(i));
            } else {
                list.add(ChatColor.MAGIC + " Coucou");
            }
        }
        return list.toString();
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }

    public static ClickableItem configCanShoot(WereWolfAPI game) {
        IConfiguration config = game.getConfig();

        return ClickableItem.of(new ItemBuilder(Material.BOW)
                .setLore(game.translate(
                        config.isConfigActive("havwila.role.hunter.can_shoot") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                .setDisplayName(game.translate("havwila.role.hunter.can_shoot"))
                .build(), e -> {
            config.setConfig("havwila.role.hunter.can_shoot", !config.isConfigActive("havwila.role.hunter.can_shoot"));

            e.setCurrentItem(new ItemBuilder(e.getCurrentItem())
                    .setLore(game.translate(config.isConfigActive("havwila.role.hunter.can_shoot") ? "werewolf.utils.enable" : "werewolf.utils.disable"))
                    .build());
        });
    }

    private class HunterClue {

        private final IPlayerWW playerWW;
        private final Location deathLocation;
        private final double distanceToOrigin;
        private final Set<IPlayerWW> nearbyPlayers;
        private List<String> playerNames;
        private int count;

        private HunterClue(IPlayerWW playerWW, Location deathLocation, Set<IPlayerWW> nearbyPlayers) {
            this.playerWW = playerWW;
            this.deathLocation = deathLocation;
            this.distanceToOrigin = deathLocation.length();
            this.nearbyPlayers = nearbyPlayers;
            this.playerNames = nearbyPlayers.stream().map(IPlayerWW::getName).collect(Collectors.toList());
            Collections.shuffle(playerNames);
            this.count = 0;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            count++;
        }

        public IPlayerWW getPlayerWW() {
            return playerWW;
        }

        public Location getDeathLocation() {
            return deathLocation;
        }

        public double getDistanceToOrigin() {
            return distanceToOrigin;
        }

        public Set<IPlayerWW> getNearbyPlayers() {
            return nearbyPlayers;
        }

        public List<String> getNamesList() {
            return playerNames;
        }


    }
}
