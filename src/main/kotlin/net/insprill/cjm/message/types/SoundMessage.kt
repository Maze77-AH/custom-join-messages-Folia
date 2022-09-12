package net.insprill.cjm.message.types

import net.insprill.cjm.CustomJoinMessages
import net.insprill.cjm.message.MessageVisibility
import org.bukkit.Sound
import org.bukkit.entity.Player

class SoundMessage(private val plugin: CustomJoinMessages) : MessageType(plugin, "sound", "Sounds") {

    override fun handle(primaryPlayer: Player, players: List<Player>, chosenPath: String, visibility: MessageVisibility) {
        val global = config.getBoolean("$chosenPath.Global")
        val soundString = config.getString("$chosenPath.Sound")
        if (enumValues<Sound>().none { it.name == soundString }) {
            plugin.logger.severe("Sound $soundString doesn't exist!")
            return
        }

        val sound = Sound.valueOf(soundString!!)

        if (global) {
            primaryPlayer.world.playSound(
                primaryPlayer.location,
                sound,
                config.getDouble("$chosenPath.Volume").toFloat(),
                config.getDouble("$chosenPath.Pitch").toFloat()
            )
            return
        }

        for (player in players) {
            player.playSound(
                player.location,
                sound,
                config.getDouble("$chosenPath.Volume").toFloat(),
                config.getDouble("$chosenPath.Pitch").toFloat()
            )
        }
    }
}
