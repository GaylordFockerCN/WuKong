# 为模组开发者提供的源码安装信息

## [ENGLISH](./README.en_US.md)

此代码遵循 Minecraft Forge 的安装方法。它会对原版的 MCP 源代码进行一些小的补丁，使你和它能够访问一些你需要的数据和函数，以构建一个成功的模组。

请注意，这些补丁是基于“未重命名”的 MCP 源代码（即 SRG 名称）构建的——这意味着你无法直接将它们与普通代码进行对比阅读。

## 设置过程

### 步骤 1

打开命令行并浏览到你解压 zip 文件的文件夹。

### 步骤 2

你有以下选择：

#### 如果你更喜欢使用 Eclipse

1. 运行以下命令：`./gradlew genEclipseRuns`
2. 打开 Eclipse，选择 Import > Existing Gradle Project > 选择文件夹
   或者运行 `gradlew eclipse` 来生成项目。

#### 如果你更喜欢使用 IntelliJ

1. 打开 IDEA，导入项目。
2. 选择你的 build.gradle 文件并导入。
3. 运行以下命令：`./gradlew genIntellijRuns`
4. 如果需要，在 IDEA 中刷新 Gradle 项目。

如果在任何时候你的 IDE 中缺少库，或者遇到问题，你可以运行 `gradlew --refresh-dependencies` 来刷新本地缓存。运行 `gradlew clean` 来重置所有内容（这不会影响你的代码），然后重新开始这个过程。

## 映射名称

默认情况下，MDK 配置使用 Mojang 提供的 Minecraft 代码库中的方法和字段的官方映射名称。这些名称受特定许可证的约束。所有模组开发者都应了解这个许可证，如果你不同意，可以在你的 build.gradle 中将映射名称更改为其他众包名称。有关最新的许可证文本，请参阅映射文件本身，或参考此处的副本：
[https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md](https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md)

## 其他资源

- 社区文档：[https://docs.minecraftforge.net/en/1.18.x/gettingstarted/](https://docs.minecraftforge.net/en/1.18.x/gettingstarted/)
- LexManos 的安装视频：[https://youtu.be/8VEdtQLuLO0](https://youtu.be/8VEdtQLuLO0)
- Forge 论坛：[https://forums.minecraftforge.net/](https://forums.minecraftforge.net/)
- Forge Discord：[https://discord.minecraftforge.net/](https://discord.minecraftforge.net/)
