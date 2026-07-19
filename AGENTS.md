# AGENTS.md

## 项目概述

fakeneko_config —— 一个处理模组配置的小型库（Fabric + NeoForge 双加载器），基于 MultiLoader-Template 构建。

本仓库为**多 MC 版本单一仓库**：通过 git worktree + 每 MC 版本一个分支管理，详见 [VERSIONS.md](VERSIONS.md)。

## 仓库结构

- `common/` —— 跨加载器共用代码（配置核心、GUI、keybind、mixin）
- `fabric/` —— Fabric 加载器入口
- `neoforge/` —— NeoForge 加载器入口
- `build-logic/` —— includeBuild 的共享 Gradle 构建逻辑（`fakeneko-multiloader-common.gradle` / `fakeneko-multiloader-loader.gradle`）
- `settings.gradle` 中 `fakeneko_common` 模块映射到 `common/` 目录

## 构建

```bash
./gradlew build          # 构建全部模块
./gradlew :fabric:build  # 仅 fabric
./gradlew :neoforge:build
```

- Gradle wrapper 指向本地 zip：`file:///D:/workspace/gradle/gradle-9.5.0-bin.zip`（fabric-loom 1.16.3 要求 Gradle ≥ 9.4）
- `gradle.properties` 新增字段时，必须同步加入 `build-logic/src/main/groovy/fakeneko-multiloader-common.gradle` 的 expandProps map
- 产物：`fabric/build/libs/fakeneko_config-fabric-{mc}-{ver}.jar`、`neoforge/build/libs/fakeneko_config-neoforge-{mc}-{ver}.jar`

## 多版本工作流（重要）

- **每 MC 版本一个分支**（如 `26.1`、`26.2`），每个版本一个独立目录（git worktree）
- 主 worktree 在 `D:\workspace\minecarftDev\26.1\fakeneko_config`，git 数据只有一份
- 开发某版本前，先确认自己处于正确的 worktree/分支：`git branch --show-current`
- 新版本接入流程、tag 命名规范、API 差异备忘 → [VERSIONS.md](VERSIONS.md)

## 发版规范（不可违反）

1. tag 命名：`v{mc版本}-{mod版本}`，如 `v26.1-1.0.5`
2. **绝不删除旧版本 tag**；只允许删除/重推当前正要发布、尚未公开的 tag
3. 发版前确认 `gradle.properties` 的 `version` 已递增

## 注意事项

- 提交信息使用中文，格式沿用历史约定（`feat:` / `fix:` / `docs:` 前缀 + 中文描述）
- worktree 的 `.git` 是文件不是目录，不要对它做手动操作
- `26.2\fakeneko_config.bak` 是迁移前的旧独立仓库备份，确认 worktree 正常后可由用户手动删除
