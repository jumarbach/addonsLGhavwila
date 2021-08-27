package io.havwila.addonsLG.roles;

import io.github.ph1lou.werewolfapi.*;
import io.github.ph1lou.werewolfapi.enums.Aura;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.Role;
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
import java.util.stream.Collectors;

public class Auramancer extends Role implements IPower {

    private Aura currentAura = Aura.NEUTRAL;
    private boolean auraLocked = false;
    private boolean power = true;
    private int counter = 0;

    public Auramancer(@NotNull WereWolfAPI game, @NotNull IPlayerWW playerWW, @NotNull String key) {
        super(game, playerWW, key);
    }

    @Override
    public @NotNull String getDescription() {
        DescriptionBuilder builder = new DescriptionBuilder(game, this).setDescription(game.translate("werewolf.role.auramancer.description",
                Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                        Formatter.format("&dark&", Aura.DARK.getChatColor() + game.translate(Aura.DARK.getKey()))))
                .addExtraLines(game.translate("werewolf.role.auramancer.aura_status",
                        Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey()))));
        switch (currentAura) {
            case LIGHT:
                builder.addExtraLines(game.translate("werewolf.role.auramancer.passive_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
                builder.addExtraLines(game.translate("werewolf.role.auramancer.active_light",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
                break;
            case NEUTRAL:
                builder.addExtraLines(game.translate("werewolf.role.auramancer.passive_neutral"));
                builder.addExtraLines(game.translate("werewolf.role.auramancer.active_neutral",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())),
                                Formatter.format("&neutral&", Aura.NEUTRAL.getChatColor() + game.translate(Aura.NEUTRAL.getKey()))));
                break;
            case DARK:
                builder.addExtraLines(game.translate("werewolf.role.auramancer.passive_dark",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
                builder.addExtraLines(game.translate("werewolf.role.auramancer.active_dark",
                        Formatter.format("&light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey()))));
        }
        return builder.build();
    }

    @Override
    public void recoverPower() {

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
        getPlayerWW().sendMessageWithKey("werewolf.role.auramancer.aura_change",
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

        IPlayerWW playerWW = event.getPlayerWW();
        playerWW.getLastKiller().ifPresent(iPlayerWW -> {
            if (this.getPlayerWW().equals(iPlayerWW)) {
                if (auraLocked) return;

                Aura auraDead = playerWW.getRole().getAura();

                if (auraDead == Aura.DARK) {
                    if (currentAura == Aura.DARK) {
                        currentAura = Aura.NEUTRAL;
                    }
                    if (currentAura == Aura.NEUTRAL) {
                        currentAura = Aura.LIGHT;
                    }
                } else {
                    currentAura = Aura.DARK;
                }
                getPlayerWW().sendMessageWithKey("werewolf.role.auramancer.aura_kill",
                        Formatter.format("&aura_dead&", auraDead.getChatColor() + game.translate(auraDead.getKey())),
                        Formatter.format("&aura_new&", currentAura.getChatColor() + game.translate(currentAura.getKey())));

            } else if (currentAura == Aura.NEUTRAL && playerWW.getLocation().distance(this.getPlayerWW().getLocation()) < 50) {
                //Compute the Aura the killer had before the kill
                Aura auraKiller = iPlayerWW.getRole().getAura();
                if (iPlayerWW.getPlayersKills().size() == 1) {
                    List<IAuraModifier> modifiers = iPlayerWW.getRole().getAuraModifiers();
                    modifiers.removeAll(modifiers.stream().filter(a -> a.getName().equals("killer")).collect(Collectors.toList()));
                    auraKiller = modifiers.size() == 0 ? iPlayerWW.getRole().getDefaultAura() : modifiers.get(modifiers.size()-1).getAura();
                }

                Aura auraVictim = iPlayerWW.getRole().getAura();
                this.getPlayerWW().sendMessageWithKey("werewolf.role.auramancer.aura_sense",
                        Formatter.format("&auraVictim&", auraVictim.getChatColor() + game.translate(auraVictim.getKey())),
                        Formatter.format("&auraKiller&", auraKiller.getChatColor() + game.translate(auraKiller.getKey())));
            }
        });
    }

    @Override
    public void second() {

        counter++;
        if (counter % 6 != 0) return;
        counter = 0;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (currentAura != Aura.LIGHT) return;

        Location location = this.getPlayerWW().getLocation();

        boolean recoverResistance = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.getAura().equals(Aura.LIGHT))
                .map(IRole::getPlayerUUID)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull).filter(player -> player.getWorld().equals(location.getWorld()) &&
                        location.distance(player.getLocation()) < 20)
                .findFirst()
                .orElse(null) != null;

        if (recoverResistance) {
            this.getPlayerWW().addPotionModifier(PotionModifier.add(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, "auramancer"));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (currentAura != Aura.DARK) return;

        if (!(event.getEntity() instanceof Player)) return;

        if (!(event.getDamager() instanceof Player)) return;

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
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (getPlayerWW().isState(StatePlayer.DEATH)) return;

        if (auraLocked) return;

        long lightAuras = game.getPlayersWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles.getAura().equals(Aura.LIGHT)).count();

        if (lightAuras == 0) {
            auraLocked = true;
            getPlayerWW().sendMessageWithKey("werewolf.role.auramancer.aura_locked",
                    Formatter.format("&aura&", currentAura.getChatColor() + game.translate(currentAura.getKey())),
                            Formatter.format("&aura_light&", Aura.LIGHT.getChatColor() + game.translate(Aura.LIGHT.getKey())));
            this.setPower(true);
        }
    }

    @Override
    public void setPower(boolean b) {
        power = b;
    }

    @Override
    public boolean hasPower() {
        return power;
    }
}
