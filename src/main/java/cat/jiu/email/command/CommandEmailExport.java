package cat.jiu.email.command;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;

import cat.jiu.email.util.EmailUtils;
import cat.jiu.email.util.JsonToStackUtil;
import cat.jiu.email.util.JsonUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

class CommandEmailExport extends CommandBase {
	public static final String Path = CommandEmailSend.Path + "export" + File.separator;
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	public String getName() {return "export";}
	public String getUsage(ICommandSender sender) {return "/email export [all]";}
	public int getRequiredPermissionLevel() {return 2;}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof EntityPlayer)) {
			throw new CommandException("email.command.export.only_player");
		}
		
		EntityPlayer player = (EntityPlayer) sender;
		if(args.length >= 1 && args[0].equals("all")) {
			JsonArray stacks = new JsonArray();
			for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
				ItemStack stack = player.inventory.getStackInSlot(i);
				if(!stack.isEmpty()) {
					stacks.add(JsonToStackUtil.toJson(stack));
				}
			}
			if(stacks.size() < 1) {
				throw new CommandException("email.command.export.all.empty_item");
			}
			
			String path = Path + "inventory"  + File.separator + dateFormat.format(new Date()) + ".json";
			JsonUtil.toJsonFile(path, stacks, false);
			player.sendMessage(EmailUtils.createTextComponent("email.command.export.all.success", TextFormatting.GREEN, dateFormat.format(new Date()) + ".json"));
		}else {
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.isEmpty()) {
				throw new CommandException("email.command.export.empty_item");
			}
			JsonArray stacks = new JsonArray();
			stacks.add(JsonToStackUtil.toJson(stack));
			
			ResourceLocation name = stack.getItem().getRegistryName();
			String path = Path + name.getResourceDomain() + "@" + name.getResourcePath() + File.separator + dateFormat.format(new Date()) + ".json";
			JsonUtil.toJsonFile(path, stacks, false);
			player.sendMessage(EmailUtils.createTextComponent("email.command.export.success", TextFormatting.GREEN, name.toString(), dateFormat.format(new Date()) + ".json"));
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return args.length == 1 ? Lists.newArrayList("all") : Collections.emptyList();
	}
}