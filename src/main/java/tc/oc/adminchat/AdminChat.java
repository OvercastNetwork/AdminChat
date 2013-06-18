package tc.oc.adminchat;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import tc.oc.adminchat.events.AdminChatEvent;

public final class AdminChat extends JavaPlugin {
    static String PERM_NODE = "chat.admin.";
    static String PERM_SEND = PERM_NODE + "send";
    static String PERM_RECEIVE = PERM_NODE + "receive";
    static String PREFIX = "[" + ChatColor.GOLD + "A" + ChatColor.WHITE + "] ";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority=EventPriority.MONITOR)
            public void broadcastAdminChat(final AdminChatEvent event) {
                Bukkit.getServer().broadcast(event.getPrefix() + event.getPlayer() + ChatColor.RESET + ": " + event.getMessage(), PERM_RECEIVE);
            }
        }, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission(PERM_SEND)) {
            sender.sendMessage(ChatColor.RED + "No permission");
        } else if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /a <msg>");
        } else {
            AdminChatEvent event = new AdminChatEvent(sender.getName(), StringUtils.join(args, " "), PREFIX);
            Bukkit.getPluginManager().callEvent(event);
        }

        return true;
    }
}
