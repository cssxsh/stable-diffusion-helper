package xyz.cssxsh.mirai.diffusion

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*

public object StableDiffusionHelper : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.stable-diffusion-helper",
        name = "stable-diffusion-helper",
        version = "0.1.0",
    ) {
        author("cssxsh")
    }
) {
    override fun onEnable() {
        StableDiffusionConfig.reload()
        StableDiffusionListener.configFolder = configFolder
        StableDiffusionListener.dataFolder = dataFolder
        StableDiffusionListener.registerTo(globalEventChannel())
    }

    override fun onDisable() {
        StableDiffusionListener.cancel()
    }
}