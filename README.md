# SmokeLess

**一个不评判你的戒烟计数器：记录，看见，然后继续。**

SmokeLess 只做三件事：看无烟时长、烟瘾来了做一次呼吸、忍不住抽了记一下。没有登录、没有联网、没有云同步、没有说教弹窗，也没有排行榜。

## 三个页面

### 1. 仪表盘

- 顶部有应用标识、当天日期和会轻微呼吸的“进行中”状态点。
- 最显眼的是渐变计时卡：天 / 小时 / 分钟，每秒用本地时间重新计算。
- 计时卡底部显示“距下一段健康变化还有 X”，并有一条流向下一里程碑的进度线。
- “身体正在发生什么”只展示最近已经到达的一个里程碑和下一个里程碑，不展开长列表。
- 有一张“烟瘾来了？”快捷卡，可以直接进入 4-7-8 呼吸页。
- 右下角有一个次要按钮“我抽了一支”。必须按住 **1.5 秒** 才生效，短按或提前松手都不会记录。
- 生效后会静默把计时起点重置为现在，并写入一条带时间戳的记录。不会弹“你又抽了”之类的提示。

### 2. 烟瘾 SOS

- 从底部导航或仪表盘的快捷卡进入后，是全屏呼吸引导：**吸气 4 秒 → 屏息 7 秒 → 呼气 8 秒**，循环 **3 轮**，约 1 分钟。
- 圆形随呼吸扩散/收缩，外圈有当前阶段进度，底部三个圆点显示轮次；阶段颜色会随吸气/屏息/呼气缓慢变化。
- 没有音乐、没有语音。
- 完成后显示一行随机短句，约 3 秒后自动回到仪表盘。
- 呼吸过程中可以用系统返回键提前离开。

### 3. 记录

- 顶部有“本周 X 支”“上周 Y 支”两张渐变摘要卡，不做总次数、排行榜、图表或趋势分析。
- 按天分组，用日期胶囊和记录卡呈现“今天 14:32 / 昨天 21:10”这样的内容。
- 长按单条记录可删除。
- 删除历史记录不会改变当前的计时起点；计时起点只由“我抽了一支”重置。

## 健康恢复时间线

以下数据固定写在 `util/HealthMilestones.kt` 中，作为 App 内的本地信息，不联网获取：

| 无烟时间 | 身体变化 |
|---|---|
| 20 分钟 | 心率和血压开始回落 |
| 8 小时 | 血液一氧化碳水平降至正常 |
| 24 小时 | 尼古丁基本代谢清除，呼吸功能初步改善 |
| 2 周 | 牙龈血液循环接近不吸烟者水平 |
| 1 个月 | 口臭改善，睡眠质量提升 |
| 3 个月 | 肺功能显著提升，呼吸道纤毛开始修复 |
| 1 年 | 冠心病风险降至持续吸烟者的一半 |
| 5 年 | 脑卒中风险趋近非吸烟者 |

仪表盘只展示已经到达和下一个里程碑，不列全表。

## 截图位置（占位）

真机或模拟器截图准备好后，放到以下位置：

- 仪表盘：`docs/screenshots/dashboard.png`（待补充）
- 烟瘾 SOS：`docs/screenshots/sos.png`（待补充）
- 记录：`docs/screenshots/log.png`（待补充）

`docs/screenshots/README.md` 也标注了同样的位置。当前仓库没有伪造截图。

## 构建方式

环境要求：

- Android Studio Ladybug（或更新版本）
- JDK 17
- Android SDK Platform 35
- Android SDK Build-Tools 35.0.0
- Gradle Wrapper 8.9（项目已内置，无需单独安装）
- 最低运行 Android 8.0（API 26）

在 Android Studio 中打开 `SmokeLess` 目录，等待 Gradle Sync 完成后运行 `app` 配置。Android Studio 会在本地生成 `local.properties`（已在 `.gitignore` 中忽略）；如果 SDK 不在默认路径，请在 Android Studio 的 SDK Manager 中设置。

命令行构建：

```bash
./gradlew assembleDebug
```

Windows 命令行：

```bat
gradlew.bat assembleDebug
```

首次构建需要联网下载 Gradle 与 Maven 依赖；**App 本身运行时不联网**。

### APK 输出路径

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 数据与隐私

- 所有数据只写入设备本地的 Room 数据库 `smokeless.db`，卸载 App 即清除。
- 没有登录、没有云同步、没有导出/导入、没有分析 SDK、没有广告 SDK。
- 没有申请通知权限、没有前台服务、没有后台唤醒、没有网络权限。
- `AndroidManifest.xml` 中没有 `INTERNET` 权限；`allowBackup=false`，避免系统把本地数据库备份到云端。
- 没有任何真实密钥、token 或个人信息。应用内时间线是公开健康信息的固定摘录，不联网获取。

## 技术栈

- Kotlin 2.0.21
- Jetpack Compose（BOM 2024.12.01）+ Material 3
- Navigation Compose
- Lifecycle / ViewModel + StateFlow
- Room 2.6.1（KSP）
- minSdk 26，targetSdk 35，compileSdk 35
- 无 Firebase、无登录、无云服务

## 项目结构

```text
SmokeLess/
  settings.gradle.kts
  build.gradle.kts
  gradle.properties
  gradlew / gradlew.bat
  gradle/wrapper/
  app/
    build.gradle.kts
    src/main/AndroidManifest.xml
    src/main/java/com/example/smokeless/
      MainActivity.kt
      data/        Room entity / dao / database / repository
      ui/          screens / theme / components / app navigation / ViewModel
      util/        time formatting / health milestones / weekly stats
    src/main/res/  theme, icon, strings
  docs/screenshots/  真实截图占位
  README.md
  .gitignore
```

## 手动验收建议

1. 首次启动，看仪表盘时长是否从 0 开始逐秒变化。
2. 短按“我抽了一支”应无反应；按住 1.5 秒后计时器归零，记录页出现一条时间戳。
3. 进入 SOS，确认 4-7-8 节奏、3 轮后短句出现并自动回仪表盘。
4. 杀进程或重启设备，确认时长和记录仍在。
5. 长按记录条目删除，确认列表更新且计时起点不变。
