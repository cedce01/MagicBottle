package vontus.magicbottle;

import net.milkbowl.vault.economy.Economy;
import org.bstats.charts.MultiLineChart;
import org.bstats.charts.SingleLineChart;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import vontus.magicbottle.config.Config;
import vontus.magicbottle.config.Messages;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bstats.bukkit.Metrics;


public class Plugin extends JavaPlugin {
	public static Logger logger;
	public Map<String,Boolean> autoEnabled = new HashMap<>();
	Economy econ = null;

	int spentXp;

	public void incrementSpentXp(int xp){
		spentXp += xp;
	}

	public int getSpentXp(){
		int output = spentXp;
		spentXp = 0;
		return output;
	}

	@Override
	public void onEnable() {
		logger = getLogger();
		logger.log(Level.INFO,"Started Magic Bottle");
		autoEnabled = new HashMap<String,Boolean>();
		setupEconomy();
		loadConfig();
		loadRepAutoContinuously();
		new Recipes(this);
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
		this.getCommand("magicbottle").setExecutor(new Commands(this));
		Metrics metrics = new Metrics(this,32395);
		metrics.addCustomChart(new SingleLineChart("xpSpent", this::getSpentXp));
	}

	public void loadRepAutoContinuously(){
		File file = new File(this.getDataFolder() + "/repairContinuously.txt");
		if(file.exists()){
            try (BufferedReader fr = new BufferedReader( new FileReader(file))){
                while (fr.ready()){
					String line = fr.readLine();
					autoEnabled.put(line.split(" ")[0],true);
				}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	public void loadConfig() {
		this.reloadConfig();

		this.saveDefaultConfig();
		Config.load(this);
		Messages.load(this);
		
		if (Config.costMoneyCraftNewBottle != 0 && econ == null) {
			logger.warning("Vault is required to set economy costs. Add Vault or set the recipe cost to 0 to disable this warning.");
			Config.costMoneyCraftNewBottle = 0;
		}
	}

	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp != null) {
				econ = rsp.getProvider();
			}
		}
	}
}
