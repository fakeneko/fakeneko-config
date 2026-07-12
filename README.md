# fakeneko_config

一个轻量、类似 MaLiLib 的 Minecraft 配置库，支持 Fabric 与 NeoForge 双平台。

## 特性

- 纯 Java，只依赖 Minecraft 与 Gson
- 支持 Boolean / Integer / Double / String / StringList / Hotkey 配置类型
- 自动 JSON 序列化与反序列化
- 自动生成配置 GUI
- 支持分类、默认值、重置、变更回调
- 支持组合键热键绑定

## 快速开始

请查看 [docs/USAGE.md](docs/USAGE.md) 获取完整接入指南。

## 项目结构

```text
fakeneko_config/
├── common/        # 核心 API 与通用实现
├── fabric/        # Fabric 加载器入口
├── neoforge/      # NeoForge 加载器入口
├── build-logic/   # Gradle 公共配置
└── docs/USAGE.md  # 接入指南
```

## 构建

```bash
./gradlew build
```

## 支持的 Minecraft 版本

- 26.2（Fabric + NeoForge）

## 许可证

MIT License

Copyright (c) 2026 fakeneko
