package me.toastedturtle.mobmoney.listeners;

import me.toastedturtle.mobmoney.MobMoney;
import me.toastedturtle.mobmoney.functions.Functions;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Random;

public class onMobDeathListener implements Listener {

    private MobMoney plugin;

    public onMobDeathListener(MobMoney mobMoney) {
        this.plugin = mobMoney;
    }

    private Random random = new Random();
    private Functions functions = new Functions();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(event.getEntity() != null && event.getEntityType() != EntityType.PLAYER) {
            LivingEntity ent = event.getEntity();
            if(ent.getKiller() != null) {

                Player killer = ent.getKiller();
                EntityType entityType = ent.getType();

                boolean rewarded = false;
                double reward = 0;

                if(plugin.config.get(entityType.toString() + ".money") != null) {
                    if(plugin.config.getDouble(entityType.toString() + ".money") != 0) {
                        reward = plugin.config.getDouble(entityType.toString() + ".money");
                        rewarded = true;
                    }
                    else {
                        if(plugin.config.get(entityType.toString() + ".min-money") != null && plugin.config.get(entityType.toString() + ".max-money") != null) {
                            double minMoney = plugin.config.getDouble(entityType.toString() + ".min-money");
                            double maxMoney = plugin.config.getDouble(entityType.toString() + ".max-money");
                            double rnd = random.nextDouble();
                            if(maxMoney > minMoney) {
                                reward = functions.round(rnd * ((maxMoney - minMoney) + minMoney), 2);

                                rewarded = true;
                            }
                            else if(minMoney > maxMoney && minMoney < 0 && maxMoney < 0){
                                reward = functions.round(rnd * ((minMoney - maxMoney) + minMoney), 2);
                                reward = reward * -1;
                                rewarded = true;
                            }

                        }
                    }


                    if(plugin.config.getStringList(entityType.toString() + ".commands") != null) {
                        if (plugin.config.getStringList(entityType.toString() + ".commands").size() > 0) {
                            List<String> cmdList = plugin.config.getStringList(entityType.toString() + ".commands");
                            if(!(cmdList.size() == 1 && cmdList.contains(null))) {
                                for (String s : cmdList) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, s.replaceFirst("/", "").replaceAll("%player%", killer.getName()).replaceAll("%mob%", entityType.getName()).replaceAll("%reward%", "" + reward));
                                }
                                rewarded = true;
                            }
                        }
                    }

                    if (rewarded) {
                        boolean earned = true;
                        if (reward > 0) {
                            plugin.econ.depositPlayer(killer, reward);
                        } else if (reward < 0) {
                            reward = reward * -1;
                            plugin.econ.withdrawPlayer(killer, reward);
                            earned = false;
                        }

                        if(!plugin.dataConfig.contains(killer.getUniqueId().toString())) {
                            String prefix = "";
                            if (plugin.config.getString("prefix") != null) {
                                prefix = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("prefix"));
                            }
                            String mobName;
                            if (plugin.config.getString(entityType.toString() + ".name") != null) {
                                mobName = plugin.config.getString(entityType.toString() + ".name");
                            } else {
                                mobName = entityType.getName();
                            }
                            String rewardMsg;
                            if(earned) {
                                rewardMsg = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("rewardMessage").replaceAll("%reward%", "" + reward).replaceAll("%mob%", mobName).replaceAll("%player%", killer.getName()));
                            }
                            else {
                                rewardMsg = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("punishedMessage").replaceAll("%reward%", "" + reward).replaceAll("%mob%", mobName).replaceAll("%player%", killer.getName()));
                            }
                            killer.sendMessage(prefix + rewardMsg);
                        }
                    }
                }
            }
        }
    }
}
