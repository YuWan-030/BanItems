# BanItems 模组

[English](./README.md) | [简体中文](./README.zh-CN.md)

一个为服务器管理员设计的 Minecraft Forge 模组，能灵活管理和限制游戏内物品，支持软封禁和硬封禁，帮助维持服务器平衡与规则。

## 核心特性
BanItems 提供针对性的物品控制和友好的管理工具，在不打扰玩家体验的前提下强制执行服务器规则：
- **双重封禁模式**：
  - **软封禁**：限制物品功能（如阻止放置方块、使用工具），但允许玩家保留物品在背包中。
  - **硬封禁**：彻底禁止玩家获取、持有或使用物品（检测到后自动移除）。
- **支持 NBT 限制**：通过识别 NBT 数据而不仅仅是物品 ID 来封禁特定物品变种（如附魔剑、自定义命名药水）。
- **直观 GUI 管理**：
  - 以游戏内物品栏样式 GUI 添加/移除封禁物品（无需手动编辑配置）。
  - 可视化预览封禁物品及其详情（如封禁类型、NBT 标签）。
- **完善的指令集**：通过游戏内指令快速访问封禁管理、查看列表和重载配置。
- **持久化配置**：封禁物品列表保存在 JSON 文件中，服务器重启后规则依然生效。

## 指令
所有指令需要 `banitems.admin` 权限（默认授予服务器管理员）。在游戏内使用 `/banitem help` 获取快速帮助：

| 指令 | 说明 |
|------|------|
| `/banitem soft` | 打开 GUI，将物品加入软封禁列表 |
| `/banitem hard` | 打开 GUI，将物品加入硬封禁列表 |
| `/banitem list soft [页码]` | 查看所有软封禁物品（支持分页，如 `/banitem list soft 2`） |
| `/banitem list hard [页码]` | 查看所有硬封禁物品（支持分页） |
| `/banitem reload` | 重载配置文件（无需重启服务器即可生效） |
| `/banitem help` | 显示所有可用指令和用法说明 |

## 安装
BanItems 是一个 Forge 模组——请确保你的服务器/客户端使用兼容的 Minecraft Forge 版本（详见 Releases 页面）：

1. 安装 Forge：下载并安装目标 Minecraft 版本的 Minecraft Forge（如 `1.20.1-forge-47.1.0`）。
2. 下载模组：从 GitHub Releases 或其他平台下载最新的 BanItems JAR 文件。
3. 将 JAR 文件放入 Mods 文件夹：
   - 服务器端：放入服务器的 `mods/` 目录。
   - 客户端（单人或联机）：放入本地 `.minecraft/mods/` 目录。
4. 启动服务器/客户端：启动 Minecraft，BanItems 会自动初始化并生成默认配置文件。

## 配置
BanItems 使用易读的 JSON 配置文件来存储封禁规则，如果需要可手动编辑：

### 配置路径
- 服务器/客户端：`config/banitems/banitems.json`

### 配置结构
首次启动时自动生成的文件包含三个关键部分：
```json
{
  "softBannedItems": [],  // 功能受限的物品（如 ["minecraft:ender_pearl"]）
  "hardBannedItems": [],  // 禁止使用/持有的物品（如 ["minecraft:creative_only_item"]）
  "excluded": []          // 从封禁物品盒中排除替换的物品 ID（如 ["minecraft:wooden_chest"]）
}

```
- **编辑提示**：推荐使用游戏内 GUI/指令进行管理——手动编辑后需执行 `/banitem reload` 才会生效。

## 开发
BanItems 遵循 Minecraft Forge 最佳实践，代码结构参考社区标准，并借鉴了 [itembarriers](https://github.com/linstarowo/itembarriers)。

### 本地构建步骤
1. **克隆仓库**：
   ```bash
   git clone https://github.com/YuWan-030/BanItems.git
   cd BanItems
2. **使用 Gradle 构建**：
   - Windows: `gradlew.bat build`
   - Linux/macOS: `./gradlew build`

**获取编译后的 JAR**：编译好的模组 JAR 文件会在 `build/libs/` 目录中（查找没有 `-sources` 或 `-dev` 后缀的文件）。

### 贡献指南
- Fork 仓库并创建功能分支（例如 `feature/add-whitelist`）。
- 遵循现有代码风格（与 Forge 模组开发规范一致）。
- 在提交 Pull Request 前请先在本地测试更改。

## 许可证
BanItems 基于 MIT License 授权——详见 LICENSE 文件完整条款。你可以：
- 在公共/私人服务器上使用此模组。
- 修改代码用于个人用途。
- 在注明来源的前提下分发修改版本。

## 致谢
- **框架**：基于 Minecraft Forge 构建，Minecraft 的主流模组开发框架。
- **灵感**：模组设计与功能参考社区反馈以及 [itembarriers](https://github.com/linstarowo/itembarriers)。
- **维护**：本项目由 YuWan-030 持续维护（查看 GitHub 仓库获取最新更新）。

如需反馈问题、提出功能请求或获得支持，请在 GitHub 提交 Issue！
