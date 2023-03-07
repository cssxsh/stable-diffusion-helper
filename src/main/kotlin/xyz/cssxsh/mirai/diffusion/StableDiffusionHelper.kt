package xyz.cssxsh.mirai.diffusion

import kotlinx.coroutines.*
import net.mamoe.mirai.console.plugin.jvm.*
import net.mamoe.mirai.event.*

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
        StableDiffusionConfig.reload()                                      // 加载config
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