package tc.oc.adminchat;

import com.github.rmsy.channels.Channel;
import com.github.rmsy.channels.ChannelsPlugin;
import com.github.rmsy.channels.PlayerManager;
import com.github.rmsy.channels.impl.SimpleChannel;
import com.google.common.base.Preconditions;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;


public final class AdminChat extends JavaPlugin {
    public static final String PERM_NODE = "chat.admin";
    public static final String PERM_SEND = AdminChat.PERM_NODE + ".send";
    public static final String PERM_RECEIVE = AdminChat.PERM_NODE + ".receive";
    /** The plugin instance. */
    private static AdminChat plugin;
    /** The admin channel. */
    private AdminChannel adminChannel;
    /** The commands manager. */
    private CommandsManager commands;
    /** The command registration. */
    private CommandsManagerRegistration commandsRegistration;

    /**
     * Gets the {@link AdminChat} instance.
     *
     * @return The {@link AdminChat} instance.
     */
    public static AdminChat get() {
        return AdminChat.plugin;
    }

    /**
     * Gets the admin channel.
     *
     * @return The admin channel.
     */
    public AdminChannel getAdminChannel() {
        return this.adminChannel;
    }

    @Command(
            aliases = "a",
            desc = "Sends a message to the administrator channel (or sets the administrator channel to your default channel).",
            max = -1,
            min = 0,
            anyFlags = true,
            usage = "[message...]"
    )
    @Console
    @CommandPermissions({AdminChat.PERM_SEND, AdminChat.PERM_RECEIVE})
    public static void onAdminChatCommand(@Nonnull final CommandContext arguments, @Nonnull final CommandSender sender) throws CommandException {
        if (Preconditions.checkNotNull(arguments, "arguments").argsLength() == 0) {
            if (Preconditions.checkNotNull(sender, "sender").hasPermission(AdminChat.PERM_RECEIVE)) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerManager playerManager = ChannelsPlugin.get().getPlayerManager();
                    Channel oldChannel = playerManager.getMembershipChannel(player);
                    Channel adminChannel = AdminChat.plugin.adminChannel;
                    playerManager.setMembershipChannel(player, adminChannel);
                    if (!oldChannel.equals(adminChannel)) {
                        sender.sendMessage(org.bukkit.ChatColor.YELLOW + get().getConfig().getString("chat.switch.success-msg", "Changed default channel to administrator chat."));
                    } else {
                        throw new CommandException(get().getConfig().getString("chat.switch.no-change-msg", "Administrator chat is already your default channel."));
                    }
                } else {
                    throw new CommandUsageException("You must provide a message.", "/a <message...>");
                }
            } else {
                throw new CommandPermissionsException();
            }
        } else if (Preconditions.checkNotNull(sender, "sender").hasPermission(AdminChat.PERM_SEND)) {
            Player sendingPlayer = null;
            if (sender instanceof Player) {
                sendingPlayer = (Player) sender;
            }
            AdminChat.plugin.adminChannel.sendMessage(arguments.getJoinedStrings(0), sendingPlayer);
            if (!sender.hasPermission(AdminChat.PERM_RECEIVE)) {
                sender.sendMessage(org.bukkit.ChatColor.YELLOW + get().getConfig().getString("chat.message-success-msg", "Message sent."));
            }
        } else {
            throw new CommandPermissionsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred. Please notify an administrator.");
            e.printStackTrace();
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }


    @Override
    public void onDisable() {
        this.commandsRegistration = null;
        this.commands = null;
        this.adminChannel = null;
        AdminChat.plugin = null;
    }

    @Override
    public void onEnable() {
        AdminChat.plugin = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.adminChannel = new AdminChannel(getConfig().getString("chat.format", ChatColor.WHITE + "[" + ChatColor.GOLD + "A" + ChatColor.WHITE + "] {1}" + ChatColor.RESET + ChatColor.WHITE + ": {2}"), AdminChat.PERM_RECEIVE);
        this.commands = new BukkitCommandsManager();
        this.commandsRegistration = new CommandsManagerRegistration(this, this.commands);
        this.commandsRegistration.register(AdminChat.class);
    }

    public final class AdminChannel extends SimpleChannel {
        public AdminChannel(String format, String permission) {
            super(format, permission);
        }
    }
}
