package fr.jojoking35.buyxp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class BuyXP extends JavaPlugin{
	private Logger log = null;
	private Economy economy = null;
	private YamlConfiguration config;
	
	public void onEnable(){
		this.log = this.getLogger();
		this.chargerConfig();
		this.setupEconomy();
	}
	
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("buyxp")){
			if(this.economy != null){
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.RED + "This command can only be run by a player.");
				}
				else{
					if(sender.hasPermission("buyxp.use")){
						if(args.length == 1){
							try{
								int buy = Integer.parseInt(args[0]);
								Player p = (Player) sender;
								double balance = this.economy.getBalance(p.getName());
								double money = this.config.getDouble("Config.OneXP") * buy;
								if(buy <= 0){
									sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + this.config.getString("Language.Usage"));
									String phrase = this.config.getString("Language.Ratio");
									phrase = phrase.replaceAll("%1", this.config.getString("Config.OneXP"));
									sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + phrase);
								}
								else if(balance >= money){
									this.economy.withdrawPlayer(sender.getName(), money);
									p.giveExp(buy);
									String phrase = this.config.getString("Language.Buy");
									phrase = phrase.replaceAll("%1", "" + buy);
									phrase = phrase.replaceAll("%2", "" + money);
									sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + phrase);
								}
								else{
									sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + this.config.getString("Language.NoMoney"));
								}
							}
							catch(NumberFormatException e){
								sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + this.config.getString("Language.Usage"));
								String phrase = this.config.getString("Language.Ratio");
								phrase = phrase.replaceAll("%1", this.config.getString("Config.OneXP"));
								sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + phrase);
							}
						}
						else{
							sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + this.config.getString("Language.Usage"));
							String phrase = this.config.getString("Language.Ratio");
							phrase = phrase.replaceAll("%1", this.config.getString("Config.OneXP"));
							sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + phrase);
						}
					}
					else{
						sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.WHITE + "You don't have permission to use this command !");
					}
				}
			}
			else{
				this.log.severe("BuyXP can't be used, please check if you have Vault and economy plugin on your server.");
				if(sender instanceof Player){
					sender.sendMessage(ChatColor.GOLD + "[BuyXP] " + ChatColor.RED + this.config.getString("Language.NotAcces"));
				}
			}
			return true;
		}
		return false;
	}
	
	private void setupEconomy(){
		try{
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if(economyProvider != null){
				this.economy = economyProvider.getProvider();
			}
		}
		catch(Error e){
			this.log.severe("You don't have Vault on your server !");
		}
    }
	
    private void chargerConfig(){
    	File file = new File(this.getDataFolder(), "config.yml");
    	if(!file.exists()){
			this.log.info("Create config file.");
    		this.config = YamlConfiguration.loadConfiguration(file);
    		
    		this.config.createSection("Config");
    		this.config.set("Config.OneXP", 2);
    		
    		this.config.createSection("Language");
    		this.config.set("Language.NotAcces", "You don't have permission to use this command !");
    		this.config.set("Language.Usage", "Usage : /buyxp <amount>");
    		this.config.set("Language.Ratio", "Ratio : 1 XP for %1$");
    		this.config.set("Language.NoMoney", "You don't have money to buy XP ...");
    		this.config.set("Language.Buy", "You buy %1 XP for %2$");
    		
    		try{
    			this.config.save(file);
    		}
    		catch(IOException ex){
    			this.log.severe("Error during config file creation !");
    		}
    		
			this.log.warning("Please config the plugin !");
    	}
    	else{
    		this.config = YamlConfiguration.loadConfiguration(file);
    	}
    }
}