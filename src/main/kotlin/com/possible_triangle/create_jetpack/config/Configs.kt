package com.possible_triangle.create_jetpack.config

import com.possible_triangle.create_jetpack.CJConstants
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec

// 26.1 Fabric port: configs go through Forge Config API Port (same ModConfigSpec API as upstream), and the
// NeoForge events / PacketDistributor are replaced by their Fabric equivalents.
object Configs {
    @Suppress("ktlint:standard:property-naming")
    var SERVER_SPEC: ModConfigSpec
        private set

    @Suppress("ktlint:standard:property-naming")
    private var LOCAL_SERVER: ServerConfig

    @Suppress("ktlint:standard:property-naming")
    internal var SYNCED_SERVER: IServerConfig? = null

    val SERVER: IServerConfig
        get() = SYNCED_SERVER ?: LOCAL_SERVER

    @Suppress("ktlint:standard:property-naming")
    var CLIENT_SPEC: ModConfigSpec
        private set

    @Suppress("ktlint:standard:property-naming")
    var CLIENT: ClientConfig
        private set

    private var server: MinecraftServer? = null

    init {
        with(ModConfigSpec.Builder().configure { ServerConfig(it) }) {
            LOCAL_SERVER = left
            SERVER_SPEC = right
        }

        with(ModConfigSpec.Builder().configure { ClientConfig(it) }) {
            CLIENT = left
            CLIENT_SPEC = right
        }
    }

    private fun syncConfig() {
        val players = server?.playerList?.players ?: return
        CJConstants.LOGGER.debug("Sending server config all players")
        players.forEach { ServerPlayNetworking.send(it, SyncConfigMessage(LOCAL_SERVER)) }
    }

    private fun syncConfig(player: ServerPlayer) {
        CJConstants.LOGGER.debug("Sending server config to ${player.scoreboardName}")
        ServerPlayNetworking.send(player, SyncConfigMessage(LOCAL_SERVER))
    }

    @JvmStatic
    fun register() {
        ConfigRegistry.INSTANCE.register(CJConstants.MOD_ID, ModConfig.Type.COMMON, SERVER_SPEC)
        ConfigRegistry.INSTANCE.register(CJConstants.MOD_ID, ModConfig.Type.CLIENT, CLIENT_SPEC)

        PayloadTypeRegistry.clientboundPlay().register(SyncConfigMessage.TYPE.type(), SyncConfigMessage.TYPE.codec())

        ServerLifecycleEvents.SERVER_STARTED.register { server = it }
        ServerLifecycleEvents.SERVER_STOPPED.register { server = null }

        ServerPlayConnectionEvents.JOIN.register { listener, _, _ -> syncConfig(listener.player) }

        ModConfigEvents.reloading(CJConstants.MOD_ID).register { config ->
            if (config.type == ModConfig.Type.COMMON) {
                syncConfig()
            }
        }
    }
}
