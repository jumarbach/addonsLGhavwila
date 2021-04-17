package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Category;
import io.github.ph1lou.werewolfapi.enums.UniversalMaterial;
import io.github.ph1lou.werewolfapi.registers.AddonRegister;
import io.github.ph1lou.werewolfapi.registers.IRegisterManager;
import io.github.ph1lou.werewolfapi.registers.RoleRegister;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.UUID;

public class Main extends JavaPlugin {

    GetWereWolfAPI ww;

    @Override
    public void onEnable() {

        ww =  getServer().getServicesManager().load(GetWereWolfAPI.class);

        IRegisterManager registerManager = ww.getRegisterManager();

        String addonKey = "werewolf.addonLGhavwila";

        registerManager.registerAddon(new AddonRegister(addonKey, "fr", this)
                .setItem(new ItemStack(UniversalMaterial.ARROW.getType()))
                .addLoreKey("werewolf.role.description")
                .addAuthors("havwila", UUID.fromString("792945f6-ce44-4039-8382-8652153fe884")));

        try {
            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.medium.display", Medium.class)
                    .addLoreKey("werewolf.role.medium.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER));

            registerManager.registerRole(new RoleRegister(addonKey, "werewolf.role.witness.display", Witness.class)
                    .addLoreKey("werewolf.role.witness.item").addCategory(Category.ADDONS).addCategory(Category.VILLAGER));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
