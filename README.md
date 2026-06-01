# Markdown Online - Markdown 在线协同编辑平台

---

## 中文版

### 项目简介

Markdown Online 是一个基于 Web 的 Markdown 文档实时协同编辑平台，支持多用户同时编辑同一文档，并提供文件管理、版本历史、实时预览等功能。系统采用前后端分离架构，由三个独立服务组成。

### 系统功能

#### 1. 用户认证
- 用户注册（邮箱 + 密码 + 昵称）
- 用户登录，基于 JWT 令牌的无状态认证
- 路由守卫：未登录用户自动跳转至登录页

#### 2. 文件管理
- 创建 Markdown 文档和纯文本文件
- 创建文件夹，支持多级目录嵌套
- 文件重命名、删除
- 文件夹导航与面包屑路径
- 支持两种内容类型：`MARKDOWN`（Markdown 文档）和 `TEXT`（纯文本）

#### 3. 实时协同编辑
- 基于 Yjs CRDT 算法的实时文档同步
- 多用户同时编辑同一文档，变更即时同步
- WebSocket 长连接，低延迟通信
- 在线用户感知（显示当前在线协作者）
- 连接状态指示（实时显示连接是否正常）

#### 4. 编辑器
- **Monaco Editor**：作为代码编辑器核心，提供语法高亮、行号、自动布局等功能
- Markdown 模式下支持语法高亮
- 支持小地图导航

#### 5. Markdown 实时预览
- 侧边栏式实时预览面板，与编辑器并排显示
- 使用 `marked` 解析 Markdown 语法
- 使用 `highlight.js` 实现代码块语法高亮
- 支持标题、表格、引用、列表、链接等完整 Markdown 语法

#### 6. 版本历史
- 自动版本快照：每 5 分钟由后端调度器自动创建版本快照
- 手动保存版本
- 版本列表浏览（显示版本号、创建者、创建时间）
- 版本恢复：选择任意历史版本恢复文档内容

### 系统架构

```
┌─────────────────────┐
│    Browser (Vue 3)  │
│  Monaco Editor      │
│  Markdown Preview   │◄──────────────────────────┐
│  Yjs Client         │                            │
└─────────┬───────────┘                            │
          │ HTTP (REST API)                        │ WebSocket (Yjs sync)
          │ /api/*                                  │ ws://host:3001
          ▼                                         │
┌─────────────────────┐                ┌────────────┴──────────┐
│  Spring Boot (8080) │                │ Collaboration Server  │
│  REST API           │                │ (Node.js, Port 3001)  │
│  JWT Auth           │◄──── HTTP ────►│  y-websocket          │
│  JPA / Hibernate    │  /internal/*   │  JWT Validation       │
│  MySQL              │                │  Document Persistence  │
└──────────┬──────────┘                └───────────────────────┘
           │
           ▼
┌─────────────────────┐
│  MySQL 8.0          │
│  5 张数据表          │
└─────────────────────┘
```

### 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **前端** | Vue 3 + TypeScript | 响应式 UI 框架 |
| | Vite | 构建工具与开发服务器 |
| | Pinia | 状态管理（auth / file / editor 三个 Store） |
| | Vue Router | SPA 路由管理 |
| | Monaco Editor | 代码编辑器（支持 Markdown/纯文本） |
| | Yjs + y-websocket + y-monaco | CRDT 实时协同框架 |
| | marked + highlight.js | Markdown 解析与代码高亮 |
| | Axios | HTTP 客户端 |
| **协同服务器** | Node.js | WebSocket 服务端 |
| | ws + y-websocket | WebSocket 服务与 Yjs 同步 |
| | Yjs | CRDT 文档模型 |
| **后端** | Spring Boot 3.2.5 | REST API 服务 |
| | Spring Security + JWT | 用户认证与授权 |
| | Spring Data JPA / Hibernate | ORM 数据访问 |
| | MySQL 8.0 | 关系型数据库 |
| | Lombok | 代码简化 |

### 项目结构

```
markdownOnLine/
├── frontend/                        # 前端 (Vue 3 + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                     # API 层
│   │   │   ├── client.ts            # Axios 实例（JWT 拦截器）
│   │   │   ├── files.ts             # 文件/文件夹 REST API
│   │   │   └── versions.ts          # 版本历史 REST API
│   │   ├── components/              # Vue 组件
│   │   │   ├── MonacoEditor.vue     # Monaco 代码编辑器（Yjs 绑定）
│   │   │   ├── MarkdownPreview.vue  # Markdown 实时预览
│   │   │   ├── CollaborationCursors.vue  # 在线用户显示
│   │   │   ├── SaveStatus.vue       # 连接状态指示
│   │   │   ├── CreateDialog.vue     # 新建文件/文件夹对话框
│   │   │   ├── FileCard.vue         # 文件卡片
│   │   │   ├── FolderCard.vue       # 文件夹卡片
│   │   │   ├── VersionPanel.vue     # 版本历史面板
│   │   │   └── Toast.vue            # 通知提示
│   │   ├── pages/                   # 页面组件
│   │   │   ├── LoginPage.vue        # 登录页
│   │   │   ├── RegisterPage.vue     # 注册页
│   │   │   ├── FileListPage.vue     # 文件列表页
│   │   │   └── EditorPage.vue       # 编辑器页（核心页面）
│   │   ├── store/                   # Pinia 状态管理
│   │   │   ├── authStore.ts         # 认证状态
│   │   │   ├── fileStore.ts         # 文件/文件夹状态
│   │   │   └── editorStore.ts       # Yjs 文档连接与协同状态
│   │   ├── router/index.ts          # 路由配置
│   │   └── styles/global.css        # 全局样式
│   ├── index.html
│   ├── vite.config.ts               # Vite 配置（含后端代理）
│   └── package.json
│
├── collaboration-server/            # 协同服务器 (Node.js)
│   └── src/
│       ├── server.js                # WebSocket 主服务（端口 3001）
│       ├── auth.js                  # JWT 令牌校验（调用后端 API）
│       └── persistence.js           # 文档持久化（通过后端 API 读写快照）
│
└── backend/                         # 后端 (Spring Boot)
    └── src/main/java/com/fileonline/
        ├── FileOnlineApplication.java
        ├── config/                  # 配置类
        │   ├── SecurityConfig.java  # Spring Security 配置
        │   ├── CorsConfig.java      # 跨域配置
        │   └── SchedulerConfig.java # 调度器配置
        ├── controller/              # REST 控制器
        │   ├── AuthController.java  # /api/auth/*  认证接口
        │   ├── FileController.java  # /api/files   文件 CRUD
        │   ├── FolderController.java# /api/folders 文件夹 CRUD
        │   ├── VersionController.java# /api/files/{id}/versions
        │   ├── InternalAuthController.java  # /internal/auth/check-token
        │   ├── InternalDocController.java   # /internal/docs/{id}/snapshot
        │   └── InternalVersionController.java
        ├── model/
        │   ├── entity/              # JPA 实体
        │   │   ├── User.java
        │   │   ├── FileEntity.java
        │   │   ├── Folder.java
        │   │   ├── DocumentSnapshot.java
        │   │   └── DocumentVersion.java
        │   └── enums/
        │       └── ContentType.java # TEXT, MARKDOWN
        ├── repository/              # JPA Repository
        ├── service/                 # 业务逻辑层
        │   ├── AuthService.java
        │   ├── FileService.java
        │   ├── FolderService.java
        │   ├── DocumentService.java
        │   └── VersionService.java
        ├── scheduler/
        │   └── VersionSnapshotScheduler.java  # 定时版本快照
        └── security/
            ├── JwtUtil.java         # JWT 工具类
            └── JwtAuthenticationFilter.java  # JWT 过滤器
```

### 数据库设计

| 表名 | 说明 |
|------|------|
| `users` | 用户账户（id, email, password_hash, nickname） |
| `folders` | 文件夹目录（id, name, parent_id, owner_id） |
| `files` | 文件元数据（id, name, content_type, owner_id, folder_id） |
| `document_snapshots` | 文档最新快照（file_id, snapshot_data — Yjs 二进制状态） |
| `document_versions` | 版本历史（file_id, snapshot_data, version_number, created_by） |

### API 接口

#### 认证 API
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录，返回 JWT |

#### 文件 API
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/files?folderId=` | 获取文件列表 |
| POST | `/api/files` | 创建文件 |
| GET | `/api/files/{id}` | 获取文件详情 |
| PUT | `/api/files/{id}` | 重命名文件 |
| DELETE | `/api/files/{id}` | 删除文件 |

#### 文件夹 API
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/folders?parentId=` | 获取文件夹列表 |
| POST | `/api/folders` | 创建文件夹 |
| DELETE | `/api/folders/{id}` | 删除文件夹 |

#### 版本 API
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/files/{id}/versions` | 获取版本列表 |
| POST | `/api/files/{id}/versions` | 创建版本快照 |
| POST | `/api/files/{id}/versions/{vid}/restore` | 恢复到指定版本 |

#### 内部 API（协同服务器调用）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/internal/auth/check-token` | 校验 JWT 令牌 |
| GET | `/internal/docs/{id}/snapshot` | 获取文档快照 |
| POST | `/internal/docs/{id}/snapshot` | 保存文档快照 |

### 实时协同工作流程

```
1. 用户打开编辑器页面
2. 前端创建 Yjs Doc 实例，注册共享文本类型 Y.Text
3. 通过 WebSocket 连接到协同服务器（携带 JWT 令牌）
4. 协同服务器验证令牌后，建立 y-websocket 连接
5. 文档状态从后端加载（通过 /internal/docs/{id}/snapshot）
6. 用户编辑内容 → Yjs CRDT 自动合并多用户变更
7. 变更通过 WebSocket 实时广播给所有在线用户
8. 协同服务器以 3 秒防抖间隔自动保存到后端
9. 最后一个用户断开连接时，执行最终保存
10. 后端调度器每 5 分钟创建版本快照
```

### 运行方式

**前置条件：** Node.js 18+、Java 17+、MySQL 8.0

```bash
# 1. 启动 MySQL，创建数据库
mysql -u root -p < backend/src/main/resources/schema.sql

# 2. 启动后端（Spring Boot，端口 8080）
cd backend
mvn spring-boot:run

# 3. 启动协同服务器（Node.js，端口 3001）
cd collaboration-server
npm install
npm start

# 4. 启动前端（Vite 开发服务器，端口 5173）
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173` 即可使用。

---

---

## English Version

### Project Overview

Markdown Online is a web-based real-time collaborative Markdown document editing platform. It supports multiple users editing the same document simultaneously, with features including file management, version history, and live preview. The system uses a decoupled architecture consisting of three independent services.

### System Features

#### 1. User Authentication
- User registration (email + password + nickname)
- User login with stateless JWT-based authentication
- Route guard: unauthenticated users are automatically redirected to the login page

#### 2. File Management
- Create Markdown documents and plain text files
- Create folders with nested directory support
- Rename and delete files
- Folder navigation with breadcrumb trail
- Two content types supported: `MARKDOWN` and `TEXT`

#### 3. Real-time Collaborative Editing
- Real-time document synchronization based on Yjs CRDT algorithm
- Multiple users can edit the same document simultaneously with instant sync
- WebSocket persistent connections for low-latency communication
- Online user awareness (displays current collaborators)
- Connection status indicator (real-time connection health)

#### 4. Editor
- **Monaco Editor** as the core code editor with syntax highlighting, line numbers, and automatic layout
- Syntax highlighting in Markdown mode
- Minimap navigation support

#### 5. Markdown Live Preview
- Side-by-side live preview panel alongside the editor
- Markdown parsing using `marked`
- Code block syntax highlighting using `highlight.js`
- Full Markdown syntax support: headings, tables, blockquotes, lists, links, etc.

#### 6. Version History
- Automatic version snapshots: backend scheduler creates snapshots every 5 minutes
- Manual version saving
- Version list browsing (version number, creator, creation time)
- Version restore: restore document content from any historical version

### System Architecture

```
┌─────────────────────┐
│    Browser (Vue 3)  │
│  Monaco Editor      │
│  Markdown Preview   │◄──────────────────────────┐
│  Yjs Client         │                            │
└─────────┬───────────┘                            │
          │ HTTP (REST API)                        │ WebSocket (Yjs sync)
          │ /api/*                                  │ ws://host:3001
          ▼                                         │
┌─────────────────────┐                ┌────────────┴──────────┐
│  Spring Boot (8080) │                │ Collaboration Server  │
│  REST API           │                │ (Node.js, Port 3001)  │
│  JWT Auth           │◄──── HTTP ────►│  y-websocket          │
│  JPA / Hibernate    │  /internal/*   │  JWT Validation       │
│  MySQL              │                │  Document Persistence  │
└──────────┬──────────┘                └───────────────────────┘
           │
           ▼
┌─────────────────────┐
│  MySQL 8.0          │
│  5 data tables      │
└─────────────────────┘
```

### Technology Stack

| Layer | Technology | Description |
|-------|-----------|-------------|
| **Frontend** | Vue 3 + TypeScript | Reactive UI framework |
| | Vite | Build tool & dev server |
| | Pinia | State management (auth / file / editor stores) |
| | Vue Router | SPA routing |
| | Monaco Editor | Code editor (Markdown / plain text) |
| | Yjs + y-websocket + y-monaco | CRDT real-time collaboration framework |
| | marked + highlight.js | Markdown parsing & code highlighting |
| | Axios | HTTP client |
| **Collaboration Server** | Node.js | WebSocket server |
| | ws + y-websocket | WebSocket service & Yjs sync |
| | Yjs | CRDT document model |
| **Backend** | Spring Boot 3.2.5 | REST API service |
| | Spring Security + JWT | Authentication & authorization |
| | Spring Data JPA / Hibernate | ORM data access |
| | MySQL 8.0 | Relational database |
| | Lombok | Code simplification |

### Project Structure

```
markdownOnLine/
├── frontend/                        # Frontend (Vue 3 + TypeScript + Vite)
│   ├── src/
│   │   ├── api/                     # API layer
│   │   │   ├── client.ts            # Axios instance (JWT interceptor)
│   │   │   ├── files.ts             # File/folder REST API
│   │   │   └── versions.ts          # Version history REST API
│   │   ├── components/              # Vue components
│   │   │   ├── MonacoEditor.vue     # Monaco code editor (Yjs binding)
│   │   │   ├── MarkdownPreview.vue  # Markdown live preview
│   │   │   ├── CollaborationCursors.vue  # Online users display
│   │   │   ├── SaveStatus.vue       # Connection status indicator
│   │   │   ├── CreateDialog.vue     # New file/folder dialog
│   │   │   ├── FileCard.vue         # File card
│   │   │   ├── FolderCard.vue       # Folder card
│   │   │   ├── VersionPanel.vue     # Version history panel
│   │   │   └── Toast.vue            # Toast notification
│   │   ├── pages/                   # Page components
│   │   │   ├── LoginPage.vue        # Login page
│   │   │   ├── RegisterPage.vue     # Registration page
│   │   │   ├── FileListPage.vue     # File list page
│   │   │   └── EditorPage.vue       # Editor page (core page)
│   │   ├── store/                   # Pinia state management
│   │   │   ├── authStore.ts         # Auth state
│   │   │   ├── fileStore.ts         # File/folder state
│   │   │   └── editorStore.ts       # Yjs doc connection & collaboration state
│   │   ├── router/index.ts          # Route configuration
│   │   └── styles/global.css        # Global styles
│   ├── index.html
│   ├── vite.config.ts               # Vite config (with backend proxy)
│   └── package.json
│
├── collaboration-server/            # Collaboration Server (Node.js)
│   └── src/
│       ├── server.js                # WebSocket server (port 3001)
│       ├── auth.js                  # JWT token validation (calls backend API)
│       └── persistence.js           # Document persistence (read/write snapshots via backend)
│
└── backend/                         # Backend (Spring Boot)
    └── src/main/java/com/fileonline/
        ├── FileOnlineApplication.java
        ├── config/                  # Configuration
        │   ├── SecurityConfig.java  # Spring Security config
        │   ├── CorsConfig.java      # CORS config
        │   └── SchedulerConfig.java # Scheduler config
        ├── controller/              # REST controllers
        │   ├── AuthController.java  # /api/auth/*   Auth endpoints
        │   ├── FileController.java  # /api/files    File CRUD
        │   ├── FolderController.java# /api/folders  Folder CRUD
        │   ├── VersionController.java# /api/files/{id}/versions
        │   ├── InternalAuthController.java  # /internal/auth/check-token
        │   ├── InternalDocController.java   # /internal/docs/{id}/snapshot
        │   └── InternalVersionController.java
        ├── model/
        │   ├── entity/              # JPA entities
        │   │   ├── User.java
        │   │   ├── FileEntity.java
        │   │   ├── Folder.java
        │   │   ├── DocumentSnapshot.java
        │   │   └── DocumentVersion.java
        │   └── enums/
        │       └── ContentType.java # TEXT, MARKDOWN
        ├── repository/              # JPA repositories
        ├── service/                 # Business logic
        │   ├── AuthService.java
        │   ├── FileService.java
        │   ├── FolderService.java
        │   ├── DocumentService.java
        │   └── VersionService.java
        ├── scheduler/
        │   └── VersionSnapshotScheduler.java  # Periodic version snapshots
        └── security/
            ├── JwtUtil.java         # JWT utility
            └── JwtAuthenticationFilter.java  # JWT filter
```

### Database Design

| Table | Description |
|-------|-------------|
| `users` | User accounts (id, email, password_hash, nickname) |
| `folders` | Folder directories (id, name, parent_id, owner_id) |
| `files` | File metadata (id, name, content_type, owner_id, folder_id) |
| `document_snapshots` | Latest document snapshots (file_id, snapshot_data — Yjs binary state) |
| `document_versions` | Version history (file_id, snapshot_data, version_number, created_by) |

### API Endpoints

#### Auth API
| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | User login, returns JWT |

#### Files API
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/files?folderId=` | List files |
| POST | `/api/files` | Create file |
| GET | `/api/files/{id}` | Get file details |
| PUT | `/api/files/{id}` | Rename file |
| DELETE | `/api/files/{id}` | Delete file |

#### Folders API
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/folders?parentId=` | List folders |
| POST | `/api/folders` | Create folder |
| DELETE | `/api/folders/{id}` | Delete folder |

#### Versions API
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/files/{id}/versions` | List versions |
| POST | `/api/files/{id}/versions` | Create version snapshot |
| POST | `/api/files/{id}/versions/{vid}/restore` | Restore to specific version |

#### Internal API (called by collaboration server)
| Method | Path | Description |
|--------|------|-------------|
| POST | `/internal/auth/check-token` | Validate JWT token |
| GET | `/internal/docs/{id}/snapshot` | Get document snapshot |
| POST | `/internal/docs/{id}/snapshot` | Save document snapshot |

### Real-time Collaboration Workflow

```
1. User opens the editor page
2. Frontend creates a Yjs Doc instance and registers a shared Y.Text type
3. Establishes WebSocket connection to collaboration server (with JWT token)
4. Collaboration server validates token and sets up y-websocket connection
5. Document state is loaded from backend (via /internal/docs/{id}/snapshot)
6. User edits content → Yjs CRDT automatically merges multi-user changes
7. Changes are broadcast to all online users via WebSocket in real-time
8. Collaboration server auto-saves to backend with 3-second debounce
9. Final save is executed when the last user disconnects
10. Backend scheduler creates version snapshots every 5 minutes
```

### Getting Started

**Prerequisites:** Node.js 18+, Java 17+, MySQL 8.0

```bash
# 1. Start MySQL and create the database
mysql -u root -p < backend/src/main/resources/schema.sql

# 2. Start the backend (Spring Boot, port 8080)
cd backend
mvn spring-boot:run

# 3. Start the collaboration server (Node.js, port 3001)
cd collaboration-server
npm install
npm start

# 4. Start the frontend (Vite dev server, port 5173)
cd frontend
npm install
npm run dev
```

Visit `http://localhost:5173` to use the application.
