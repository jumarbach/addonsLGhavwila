package io.havwila.addonsLG.roles;

import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.day_cycle.DayEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.player.utils.Formatter;
import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.role.impl.Role;
import fr.ph1lou.werewolfapi.player.impl.PotionModifier;
import fr.ph1lou.werewolfapi.player.interfaces.IAuraModifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Auramancer extends Role {

    private Aura currentAura = Aura.NEUTRAL;
    private boolean auraLocked = false;
    private IPlayerWW knownPlayer = null;

    public Auramancer(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW, @NotNull String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        DescriptionBuilder builder = new DescriptionBuilder(game, this).setDescription(game.translate("havwila.role.auramancer.description",
                Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                        Formatter.format("&dark&", Aura.DARK.getChatColor() + game.translate(Aura.DARK.getKey()))))
                .addExtraLines(game.translate("havwila.role.auramancer.aura_status",
                        Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey()))));
        switch (currentAura) {
            case LIGHT:
                builder.addExtraLines(game.translate("havwila.role.auramancer.passive_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
                break;
            case NEUTRAL:
                builder.addExtraLines(game.translate("havwila.role.auramancer.passive_neutral"));
                break;
            case DARK:
                builder.addExtraLines(game.translate("havwila.role.auramancer.passive_dark",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
        }
        if (currentAura.equals(Aura.DARK)) {
            if (auraLocked) {
                builder.addExtraLines(game.translate("havwila.role.auramancer.no_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
            } else if (knownPlayer != null) {
                if (!knownPlayer.getRole().getAura().equals(Aura.LIGHT)) {
                    knownPlayer = null;
                } else {
                    builder.addExtraLines(game.translate("havwila.role.auramancer.light_player",
                            Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                            Formatter.format("&player&", knownPlayer.getName())));
                }
            }
            if (knownPlayer == null) {
                List<IPlayerWW> lightPlayers = game.getPlayersWW()
                        .stream()
                        .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                        .filter(playerWW2 -> !playerWW2.equals(getPlayerWW()))
                        .filter(playerWW2 -> playerWW2.getRole().getAura().equals(Aura.LIGHT))
                        .collect(Collectors.toList());

                if (lightPlayers.isEmpty()) {
                    auraLocked = true;
                    getPlayerWW().sendMessageWithKey("havwila.role.auramancer.aura_locked",
                            Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey())),
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
                    if (isAbilityEnabled()) {
                        getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, "auramancer_speed"));
                    }
                } else {
                    knownPlayer = lightPlayers.get(game.getRandom().nextInt(lightPlayers.size()));
                    getPlayerWW().sendMessageWithKey("havwila.role.auramancer.light_player",
                            Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                            Formatter.format("&player&", knownPlayer.getName()));
                }
            }
        }
        return builder.build();
    }

    @Override
    public void recoverPower() {
        if (auraLocked && isAbilityEnabled()) {
            getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, "auramancer_speed"));
        }
    }

    @Override
    public Aura getDefaultAura() {
        return currentAura;
    }

    @Override
    public Aura getAura() {
        return currentAura;
    }

    @Override
    public void addAuraModifier(IAuraModifier auraModifier) {

        if (auraLocked) return;

        if (auraModifier.getName().equals("killer")) return;

        currentAura = auraModifier.getAura();
        getPlayerWW().sendMessageWithKey("havwila.role.auramancer.aura_change",
                Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey())));
    }

    @Override
    public void removeAuraModifier(IAuraModifier auraModifier) {
    }

    @Override
    public void removeAuraModifier(String modifierName) {
    }

    @Override
    public void removeTemporaryAuras() {
    }

    @Override
    public List<IAuraModifier> getAuraModifiers() {
        return new ArrayList<>();
    }

    @Override
    public boolean isNeutral() {
        return super.isNeutral() || (currentAura == Aura.DARK && !super.isWereWolf());
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event) {
        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.getLastKiller().ifPresent(killerWW -> {

            if (currentAura == Aura.NEUTRAL && killerWW.getLocation().distance(this.getPlayerWW().getLocation()) < 50) {
                //Compute the Aura the killer had before the kill
                Aura auraKiller = killerWW.getRole().getAura();
                if (killerWW.getPlayersKills().size() == 1) {
                    List<IAuraModifier> modifiers = killerWW.getRole().getAuraModifiers();
                    modifiers.removeAll(modifiers.stream().filter(a -> a.getName().equals("killer")).collect(Collectors.toList()));
                    auraKiller = modifiers.size() == 0 ? killerWW.getRole().getDefaultAura() : modifiers.get(modifiers.size()-1).getAura();
                }

                Aura auraVictim = killerWW.getRole().getAura();
                this.getPlayerWW().sendMessageWithKey("havwila.role.auramancer.aura_sense",
                        Formatter.format("&auraVictim&", auraVictim.getChatColor() + game.translate(auraVictim.getKey())),
                        Formatter.format("&auraKiller&", auraKiller.getChatColor() + game.translate(auraKiller.getKey())));
            }
            if (this.getPlayerWW().equals(killerWW)) {
                if (auraLocked) return;

                Aura auraDead = playerWW.getRole().getAura();

                if (auraDead == Aura.DARK) {
                    if (currentAura == Aura.DARK) {
                        currentAura = Aura.NEUTRAL;
                    }
                    else if (currentAura == Aura.NEUTRAL) {
                        currentAura = Aura.LIGHT;
                    }
                } else {
                    currentAura = Aura.DARK;
                }
                getPlayerWW().sendMessageWithKey("havwila.role.auramancer.aura_kill",
                        Formatter.format("&aura_dead&", auraDead.getChatColor() + game.translate(auraDead.getKey())),
                        Formatter.format("&aura_new&", currentAura.getChatColor() + game.translate(currentAura.getKey())));

            }
        });
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

        if (currentAura.equals(Aura.DARK)) {
            Player damager = (Player) event.getDamager();
            IPlayerWW damagerWW = game.getPlayerWW(damager.getUniqueId()).orElse(null);
            //also handles case damagerWW == null
            if (!damagerWW.equals(getPlayerWW())) return;

            Player target = (Player) event.getEntity();
            IPlayerWW targetWW = game.getPlayerWW(target.getUniqueId()).orElse(null);
            if (targetWW == null) return;

            if (targetWW.getRole().getAura().equals(Aura.LIGHT)) {
                event.setDamage(event.getDamage() * (1 + game.getConfig().getStrengthRate() / 100f));
            }
        } else if (currentAura.equals(Aura.LIGHT)) {
            if (!isAbilityEnabled()) return;
            Set<IPlayerWW> nearbyPlayers = game.getPlayersWW()
                    .stream()
                    .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                    .filter(playerWW2 -> !playerWW2.equals(getPlayerWW()))
                    .filter(playerWW2 -> playerWW2.getRole().getAura().equals(Aura.LIGHT))
                    .filter(playerWW2 -> playerWW2.getLocation().distance(getPlayerWW().getLocation()) < 50)
                    .collect(Collectors.toSet());

            if (nearbyPlayers.contains(event.getDamager())) {
                event.setDamage(event.getDamage() * (1 + game.getConfig().getStrengthRate() / 200f));
            }
        }


    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (auraLocked) return;

        if (!currentAura.equals(Aura.DARK)) return;

        long lightAuras = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.getAura().equals(Aura.LIGHT)).count();

        if (lightAuras == 0) {
            auraLocked = true;
            getPlayerWW().sendMessageWithKey("havwila.role.auramancer.aura_locked",
                    Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey())),
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
            if (isAbilityEnabled()) {
                getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.SPEED, "auramancer_speed"));
            }
        }
    }

    @EventHandler
    public void onDay(DayEvent event) {
        if (!getAura().equals(Aura.LIGHT)) {
            return;
        }

        if (!isAbilityEnabled()) return;

        Set<IPlayerWW> nearbyPlayers = game.getPlayersWW()
                .stream()
                .filter(playerWW2 -> playerWW2.isState(StatePlayer.ALIVE))
                .filter(playerWW2 -> !playerWW2.equals(getPlayerWW()))
                .filter(playerWW2 -> playerWW2.getLocation().distance(getPlayerWW().getLocation()) < 50)
                .collect(Collectors.toSet());

        nearbyPlayers.forEach(nearbyWW -> nearbyWW.getRole().removeAuraModifier("killer"));
    }
}
