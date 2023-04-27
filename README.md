# [Stable Diffusion Helper](https://github.com/cssxsh/stable-diffusion-helper)

> 基于 [Stable Diffusion web UI](https://github.com/AUTOMATIC1111/stable-diffusion-webui) 的 图片生成插件

[![maven-central](https://img.shields.io/maven-central/v/xyz.cssxsh/stable-diffusion-helper)](https://search.maven.org/artifact/xyz.cssxsh/stable-diffusion-helper)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/1657)

**使用前应该查阅的相关文档或项目**

*   [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
*   [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
*   [Stable Diffusion web UI Wiki](https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki/API)

本插件对接的是 `Stable Diffusion web UI` 的 REST API, 需要启动配置中开启选项, 详情请自行查询 [WIKI](https://github.com/AUTOMATIC1111/stable-diffusion-webui/wiki/API)  

## 使用

更多内容请看 [详细介绍](MORE.md)

### `styles`

`Styles` 是 `Stable Diffusion web UI` 自带的功能，用于快捷的填充 `prompt` 和 `negative_prompt`  
![Styles.png](.github/Styles.png)

### `t2i`

以文生图  
权限ID: `xyz.cssxsh.mirai.plugin.stable-diffusion-helper:txt2img`  
例子:  
![t2i](.github/t2i.png)

### `i2i`

以图生图  
权限ID: `xyz.cssxsh.mirai.plugin.stable-diffusion-helper:img2img`  
例子:  
![i2i](.github/i2i.png)

## 配置

`client.yml` 基本配置

* `base_url` 基本网址
* `dns_over_https` DNS
* `timeout` API超时时间
* `cool_down_time` API冷却时间

## 安装

### MCL 指令安装

**请确认 mcl.jar 的版本是 2.1.0+**  
`./mcl --update-package xyz.cssxsh:stable-diffusion-helper --channel maven-stable --type plugins`

### 手动安装

1.  从 [Releases](https://github.com/cssxsh/stable-diffusion-helper/releases) 或者 [Maven](https://repo1.maven.org/maven2/xyz/cssxsh/mirai/stable-diffusion-helper/) 下载 `mirai2.jar`
2.  将其放入 `plugins` 文件夹中

## [爱发电](https://afdian.net/@cssxsh)

![afdian](.github/afdian.jpg)