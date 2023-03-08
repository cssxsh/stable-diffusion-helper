package xyz.cssxsh.mirai.diffusion.config

import net.mamoe.mirai.console.data.*
import xyz.cssxsh.diffusion.StableDiffusionTextToImageBuilder

@PublishedApi
internal object TextToImageConfig : ReadOnlyPluginConfig("TextToImage") {

    // region http body

    @ValueName("width")
    @ValueDescription("默认宽度")
    val width: Int by value(512)

    @ValueName("height")
    @ValueDescription("默认高度")
    val height: Int by value(512)

    @ValueName("steps")
    @ValueDescription("默认steps")
    val steps: Int by value(20)

    @ValueName("cfg_scale")
    @ValueDescription("默认cfg_scale")
    val cfg_scale: Double by value(7.0)

    @ValueName("sampler")
    @ValueDescription("默认sampler")
    val sampler_name: String by value("Euler a")

    @ValueName("batch_size")
    @ValueDescription("默认图片生成轮数")
    val batch_size: Int by value(1)

    @ValueName("n_iters")
    @ValueDescription("默认每轮生成图片张数")
    val n_iters: Int by value(1)

    // endregion

    @ValueName("Detailed Output")
    @ValueDescription("(ture/false)true时以合并转发形式输出详细信息，否则只输出图片")
    val Detailed_output: Boolean by value(false)

    fun push(builder: StableDiffusionTextToImageBuilder) {
    {
        builder.width = width
        builder.height = height
        builder.steps = steps
        builder.cfgScale = cfg_scale
        builder.samplerName = sampler_name
        builder.batchSize = batch_size
        builder.nIter = n_iters

    }

}