package net.insprill.cjm.listener

import net.insprill.cjm.CustomJoinMessages
import net.insprill.cjm.test.MessageTypeMock
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockbukkit.mockbukkit.MockBukkit
import org.mockbukkit.mockbukkit.ServerMock
import org.mockbukkit.mockbukkit.entity.PlayerMock

class JoinEventTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: CustomJoinMessages
    private lateinit var messageTypeMock: MessageTypeMock

    @BeforeEach
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(CustomJoinMessages::class.java)
        messageTypeMock = MessageTypeMock(plugin)
        plugin.messageSender.registerType(messageTypeMock)
        // Wacky MockBukkit permissions go brr
        server.pluginManager.addPermission(Permission("cjm.default", PermissionDefault.TRUE))
    }

    @AfterEach
    fun teardown() {
        MockBukkit.unmock()
    }

    @Test
    fun onPlayerJoin_FirstJoin_SendsFirstJoinMessage() {
        server.addPlayer()

        messageTypeMock.assertHasResult()
        assertTrue(messageTypeMock.result.chosenPath.contains(".First-Join.", true))
    }

    @Test
    fun onPlayerJoin_Join_SendsFirstJoinMessage() {
        val player = server.addPlayer()
        player.disconnect()
        messageTypeMock.clearResults()
        player.reconnect()

        messageTypeMock.assertHasResult()
        assertTrue(messageTypeMock.result.chosenPath.contains(".Join.", true))
    }

    @Test
    fun onPlayerJoin_Vanished_NoMessageSent() {
        val player = PlayerMock(server, "player")
        player.setMetadata("vanished", FixedMetadataValue(plugin, true))
        server.addPlayer(player)

        messageTypeMock.assertDoesntHaveResult()
    }

    @Test
    fun onPlayerJoin_WorldBasedMessagesEnabled_MarksWorldAsJoined() {
        plugin.config.set("World-Based-Messages.Enabled", true)

        val player = server.addPlayer()

        assertTrue(plugin.worldChangeEvent.saveVisitedWorld(player, player.world))
    }

    @Test
    fun onPlayerJoin_WorldBasedMessagesDisabled_DoesntMarkWorldAsJoined() {
        val player = server.addPlayer()

        assertFalse(plugin.worldChangeEvent.saveVisitedWorld(player, player.world))
    }

    @Test
    fun onPlayerJoin_ClearsDefaultMessage() {
        server.addPlayer()

        server.pluginManager.assertEventFired(PlayerJoinEvent::class.java) { it.joinMessage() == null }
    }

}
