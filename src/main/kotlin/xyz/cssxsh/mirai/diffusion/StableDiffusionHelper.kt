package xyz.cssxsh.mirai.diffusion

import kotlinx.coroutines.cancel
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.registerTo
import xyz.cssxsh.mirai.diffusion.config.ImageToImageConfig
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
        ImageToImageConfig.reload()

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
