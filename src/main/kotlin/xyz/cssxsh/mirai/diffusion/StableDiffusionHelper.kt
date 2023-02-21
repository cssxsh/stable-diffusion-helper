package xyz.cssxsh.mirai.diffusion

import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.utils.*

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
        logger.info { "Plugin loaded" }
    }
}