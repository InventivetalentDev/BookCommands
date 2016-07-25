package org.inventivetalent.bookcommands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookCommands extends JavaPlugin implements Listener {

	String  bookTitle     = "§eCommand Book";
	boolean requireSigned = true;

	List<String> instructions = Arrays.asList("§aWrite your command and sign this book when you're done", "§aSneak + Right-click the book to run your command");

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		saveDefaultConfig();
		bookTitle = getConfig().getString("bookTitle");
		requireSigned = getConfig().getBoolean("requireSigned");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cYou need to be a player");
			return false;
		}
		if (!sender.hasPermission("bookcommands.get")) {
			sender.sendMessage("§cNo permission");
			return false;
		}

		ItemStack itemStack = new ItemStack(Material.BOOK_AND_QUILL);
		BookMeta meta = (BookMeta) itemStack.getItemMeta();
		meta.setDisplayName(bookTitle);
		meta.setLore(instructions);
		meta.setPages("");
		itemStack.setItemMeta(meta);

		((Player) sender).getInventory().addItem(itemStack);
		for (String s : instructions) {
			sender.sendMessage(s);
		}

		return true;
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getPlayer().isSneaking()) {
			if (event.getItem() != null) {
				if ((requireSigned && event.getItem().getType() == Material.WRITTEN_BOOK) || (!requireSigned && event.getItem().getType() == Material.BOOK_AND_QUILL)) {
					if (event.getItem().hasItemMeta()) {
						if (bookTitle.equals(event.getItem().getItemMeta().getDisplayName())) {
							if (!event.getPlayer().hasPermission("bookcommands.use")) {
								event.getPlayer().sendMessage("§cNo permission");
								return;
							}

							BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();

							List<String> commands = new ArrayList<>();
							for (int i = 0; i < bookMeta.getPageCount(); i++)
								commands.add("");

							int c = 0;
							for (String page : bookMeta.getPages()) {
								if (commands.size() > 1 && page.startsWith("/")) { c++; }
								commands.set(c, commands.get(c) + page);
							}

							for (String cmd : commands) {
								if (cmd.length() > 0) {
									event.getPlayer().chat(cmd);
								}
							}
						}
					}
				}
			}
		}
	}

}
