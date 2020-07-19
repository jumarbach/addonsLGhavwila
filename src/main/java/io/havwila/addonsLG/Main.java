package io.havwila.addonsLG;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.RoleRegister;
import io.github.ph1lou.werewolfapi.enumlg.Category;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class Main extends JavaPlugin {

    public static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public GetWereWolfAPI ww;

    @Override
    public void onEnable() {
        instance = this;

        ww = (GetWereWolfAPI) Bukkit.getPluginManager().getPlugin("WereWolfPlugin");

        ww = (GetWereWolfAPI) Bukkit.getPluginManager().getPlugin("WereWolfPlugin");

        ww.loadTranslation(this,"fr");
        ww.getAddonsList().add(this);

        try {
            RoleRegister medium = new RoleRegister(this,ww,"werewolf.role.medium.display").registerRole(Medium.class);
            medium.setLore(Arrays.asList("§fMedium","Par havwila")).addCategory(Category.ADDONS).addCategory(Category.VILLAGER).create();

            RoleRegister witness = new RoleRegister(this,ww,"werewolf.role.witness.display").registerRole(Witness.class);
            witness.setLore(Arrays.asList("§fTémoin","Par havwila")).addCategory(Category.ADDONS).addCategory(Category.VILLAGER).create();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
