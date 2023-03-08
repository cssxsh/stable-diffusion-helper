package xyz.cssxsh.mirai.diffusion

import kotlinx.coroutines.*
import net.mamoe.mirai.console.data.PluginConfig
import net.mamoe.mirai.console.plugin.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*
import net.mamoe.mirai.utils.*
import xyz.cssxsh.mirai.diffusion.config.TextToImageConfig

public object StableDiffusionHelper : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.cssxsh.mirai.plugin.stable-diffusion-helper",
        name = "stable-diffusion-helper",
        version = "0.2.0",
    ) {
        author("cssxsh")
    }
) {

    override fun onEnable() {

        StableDiffusionConfig.reload()
        TextToImageConfig.reload()

        StableDiffusionListener.configFolder
        StableDiffusionListener.dataFolder
        StableDiffusionListener.reload
        StableDiffusionListener.txt2img
        StableDiffusionListener.img2img
        StableDiffusionListener.styles
        StableDiffusionListener.registerTo(globalEventChannel())
    }

    override fun onDisable() {
        StableDiffusionListener.cancel()
    }
}
