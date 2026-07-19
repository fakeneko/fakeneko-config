# fakeneko_config 多版本管理

本仓库通过 **git worktree + 每 MC 版本一个分支** 管理 fakeneko_config 模组的所有 Minecraft 版本。

## 目录结构

```
D:\workspace\minecarftDev\
├── 26.1\
│   └── fakeneko_config\        ← 主 worktree（git 数据在此），签出 26.1 分支
├── 26.2\
│   ├── fakeneko_config\        ← worktree，签出 26.2 分支
│   └── fakeneko_config.bak\    ← 原独立仓库备份（确认无误后可删除）
├── 1.21\
│   └── fakeneko_config\        ← 【未来】worktree，签出 1.21.x 分支
└── 1.18 / 1.19 / 1.20\         ← 【未来】同样方式接入
```

- git 数据只有一份，所有 worktree 共享提交历史和 tag
- 每个目录签出不同分支，IDEA 各自独立打开，可并行开发
- 同一分支不能同时被两个 worktree 签出

## 分支 / tag 命名规范

| 项 | 规范 | 示例 |
|---|---|---|
| 分支 | 以 MC 版本命名 | `26.1`、`26.2`、`1.20.1` |
| tag | `v{mc版本}-{mod版本}` | `v26.1-1.0.5`、`v26.2-1.0.5` |
| mod 版本 | `gradle.properties` 的 `version` 字段，各分支独立演进 | `1.0.5` |

**发版铁律：不删除旧版本 tag。** 只允许删除/重推"当前正要发布、尚未公开"的那个 tag；新版本直接在对应分支上建新 tag。

## 当前版本一览

| 分支 | MC 版本 | 加载器 | 模板来源 | Java | Gradle | 状态 |
|---|---|---|---|---|---|---|
| `26.2` | 26.2 | Fabric + NeoForge | MultiLoader-Template（新版） | 25 | 9.5.0 | ✅ 基线 |
| `26.1` | 26.1 | Fabric + NeoForge | 由 26.2 分支改造 | 25 | 9.5.0 | ✅ 已适配 |

## 新版本接入 checklist

### 26.x 系（基于新版模板，Fabric + NeoForge）

1. 在主 worktree（`26.1\fakeneko_config`）执行：
   ```bash
   git worktree add D:\workspace\minecarftDev\<版本>\fakeneko_config -b <版本> 26.2
   ```
   （新分支基于最近的 26.x 分支切出，通常继承最新功能）
2. 修改 `gradle.properties`：`minecraft_version`、`minecraft_version_range`、`neo_form_version`、`fabric_version`、`fabric_loader_version`、`neoforge_version`，以及 cloth_config/yacl/modmenu 对应版本
3. 确认 `gradle/wrapper/gradle-wrapper.properties` 的本地 gradle zip（注意 fabric-loom 对 Gradle 最低版本的要求）
4. `./gradlew build` 验证，修复 MC API 差异（如 26.1→26.2 的 `gui.setScreen` 变更）
5. 提交到该版本分支，并在本文档"当前版本一览"登记

### 1.18 ~ 1.21 系（基于历史 Forge 模板）

1. 拿到对应年代的历史 MultiLoader 模板（1.18~1.20.4 为 Forge + Fabric，1.21 参考 `1.21\auto_switch_elytra` 的 forge/neoforge 结构）
2. 以模板为基础建立 orphan 分支或直接提交为该版本分支的首个提交：
   ```bash
   git worktree add D:\workspace\minecarftDev\<版本>\fakeneko_config -b <版本>
   # 在空分支上复制模板文件，再把 common 源码按旧 API 适配迁移
   ```
3. 适配源码 API 差异（工作量主要在 Screen/输入/注册相关 API）
4. 构建验证、提交、登记

## 常用命令

```bash
# 查看所有版本 worktree
git worktree list

# 添加新版本 worktree（以 26.2 分支为基）
git worktree add D:\workspace\minecarftDev\<版本>\fakeneko_config -b <分支名> 26.2

# 移除某个版本的 worktree（不删分支）
git worktree remove D:\workspace\minecarftDev\<版本>\fakeneko_config

# 发版（在对应版本目录内）
git tag v<mc版本>-<mod版本>
```

## 各版本 API 差异备忘

| 差异 | 26.1 | 26.2 |
|---|---|---|
| 打开 Screen | `Minecraft.getInstance().setScreen(s)` | `Minecraft.getInstance().gui.setScreen(s)` |
| 当前 Screen | `Minecraft.getInstance().screen`（字段） | `Minecraft.getInstance().gui.screen()`（方法） |
| fabric-loom 1.16.3 | 要求 Gradle ≥ 9.4 | 同左 |
