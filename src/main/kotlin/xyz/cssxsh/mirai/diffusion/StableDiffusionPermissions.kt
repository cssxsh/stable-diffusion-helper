package xyz.cssxsh.mirai.diffusion

import net.mamoe.mirai.console.permission.*
import net.mamoe.mirai.event.*
import kotlin.properties.*
import kotlin.reflect.*

@PublishedApi
internal object StableDiffusionPermissions : ReadOnlyProperty<ListenerHost, Permission> {
    private val records: MutableMap<String, Permission> = HashMap()

    @Synchronized
    override fun getValue(thisRef: ListenerHost, property: KProperty<*>): Permission {
        return records[property.name] ?: StableDiffusionHelper.runCatching {
            val permission = PermissionService.INSTANCE.register(
                id = permissionId(property.name),
                description = "触发 ${property.name}",
                parent = parentPermission
            )

            records[property.name] = permission

            logger.info("${property.name} 权限 ${permission.id}")

            permission
        }.getOrElse {
            PermissionService.INSTANCE.rootPermission
        }
    }
}