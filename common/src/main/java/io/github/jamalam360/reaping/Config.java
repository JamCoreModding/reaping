package io.github.jamalam360.reaping;

import io.github.jamalam360.jamlib.config.ConfigExtensions;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Config implements ConfigExtensions<Config> {
	public boolean allowReapingPlayers = true;

	@Override
	public List<Link> getLinks() {
		return List.of(
				new Link(Link.DISCORD, "https://jamalam.tech/Discord", Component.translatable("config.reaping.discord")),
				new Link(Link.GITHUB, "https://github.com/JamCoreModding/reaping", Component.translatable("config.reaping.github")),
				new Link(Link.GENERIC_LINK, "https://modrinth.com/mod/reaping", Component.translatable("config.reaping.modrinth"))
		);
	}
}
