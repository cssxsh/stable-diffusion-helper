# [Stable Diffusion Helper](https://github.com/cssxsh/stable-diffusion-helper)

> 基于 [Stable Diffusion web UI](https://github.com/AUTOMATIC1111/stable-diffusion-webui) 的 图片生成插件

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh.mirai/mirai-openai-plugin)](https://search.maven.org/artifact/xyz.cssxsh.mirai/mirai-openai-plugin)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/1657)

**使用前应该查阅的相关文档或项目**

*   [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
*   [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
*   [Stable Diffusion web UI Wiki](https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki/API)

本插件对接的是 `Stable Diffusion web UI` 的 REST API, 请在启动配置中开启选项，详情请自行查询 Stable Diffusion web UI Wiki

## 使用

### `t2i`

以文生图  
权限ID: `xyz.cssxsh.mirai.plugin.stable-diffusion-helper:txt2img`  
例子:
```log
t2i 
steps=50
width=360
height=540
#德克萨斯
night, rain, wet
```
```log
t2i 123456
(8k, RAW photo, best quality, masterpiece:1.2), (realistic, photo-realistic:1.37),omertosa,1girl,(Kpop idol), 
(aegyo sal:1),cute,cityscape, night, rain, wet, professional lighting, photon mapping, radiosity, 
physically-based rendering, <lora:arknightsTexasThe_v10:1>, <lora:koreanDollLikeness_v10:0.5>,Black pantyhose
```

* 设置种子 `t2i $seed`
* 设置参数 `key=value`
* 使用Styles `#xxx`
* 第二行开始才会计入 `prompt`, 所以要两行以上才会触发指令

支持的参数 
* `height` Height `360`
* `width` Width `540`
* `sampler_name` Sampling method `Euler a`
* `steps` Sampling steps `32`
* `batch_size` Batch size `1`
* `n_iter` Batch count `1`
* `cfg_scale` CFG Scale `数字`
* `restore_faces` Restore faces `false`/`true`
* `tiling` Tiling `false`/`true`
* `enable_hr` Hires. fix `false`/`true`
* `hr_second_pass_steps` Hires Steps `0`
* `denoising_strength` Denoising strength `0.7`
* `hr_upscaler` Upscaler `Latent`
* `hr_scale` Upscale by `2.0`

### `styles`

查看已经载入的 `Styles`  
权限ID: `xyz.cssxsh.mirai.plugin.stable-diffusion-helper:styles`  
例子:
```log
styles 
```
```log
风格 
```

`Styles` 是 `Stable Diffusion web UI` 自带的功能，用于快捷的填充 `prompt` 和 `negative_prompt`  
![Styles.png](.github/Styles.png)

### `重载SD`

重载`client.yml`  
权限ID: `xyz.cssxsh.mirai.plugin.stable-diffusion-helper:reload`  
例子:
```log
重载SD
```

## 配置

`client.yml` 基本配置

* `base_url` 基本网址
* `dns_over_https` DNS
* `timeout` API超时时间

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh:stable-diffusion-helper --channel maven-stable --type plugins`

### 手动安装

1.  从 [Releases](https://github.com/cssxsh/stable-diffusion-helper/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/mirai/stable-diffusion-helper/) 下载 `mirai2.jar`
2.  将其放入 `plugins` 文件夹中

## [爱发电](https://afdian.net/@cssxsh)

![afdian](.github/afdian.jpg)