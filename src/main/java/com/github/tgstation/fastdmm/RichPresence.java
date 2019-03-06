package com.github.tgstation.fastdmm;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class RichPresence {
	static void start() {
	    DiscordRPC lib = DiscordRPC.INSTANCE;
	    String applicationId = "551596700662366216";
	    DiscordEventHandlers handlers = new DiscordEventHandlers();
	    lib.Discord_Initialize(applicationId, handlers, true, null);
	    DiscordRichPresence presence = new DiscordRichPresence();
	    presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
	    presence.details = "Mapping with QDMM";
	    presence.largeImageKey = "qdmm";
	    presence.largeImageText = "QwertyDMM";
	    lib.Discord_UpdatePresence(presence);

	    new Thread(() -> {
	        while (!Thread.currentThread().isInterrupted()) {
	            lib.Discord_RunCallbacks();
	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException ignored) {}
	        }
	    }, "RPC-Callback-Handler").start();
	}
}
