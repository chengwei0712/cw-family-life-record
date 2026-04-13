# 家庭记忆 - 移动端 App

基于 UniApp + Vue 3 开发的家庭记忆系统移动端应用。

## 功能特性

- ✅ 用户登录/注册
- ✅ 照片视频管理（上传、浏览、删除）
- 🚧 日记本功能（开发中）
- 🚧 个人中心（开发中）

## 技术栈

- **框架**: UniApp (Vue 3)
- **语言**: TypeScript
- **状态管理**: Pinia
- **构建工具**: Vite

## 项目结构

```
baby-picture-app/
├── src/
│   ├── api/              # API 接口
│   │   ├── user.ts       # 用户相关接口
│   │   └── media.ts      # 媒体相关接口
│   ├── pages/            # 页面
│   │   ├── login/        # 登录注册页
│   │   ├── media/        # 照片视频页
│   │   ├── diary/        # 日记本页
│   │   └── profile/      # 个人中心页
│   ├── store/            # 状态管理
│   │   └── user.ts       # 用户状态
│   ├── utils/            # 工具函数
│   │   └── request.ts    # 请求封装
│   ├── static/           # 静态资源
│   ├── App.vue           # 应用入口
│   └── main.ts           # 主入口文件
├── manifest.json         # UniApp 配置
├── pages.json            # 页面路由配置
├── vite.config.ts        # Vite 配置
├── tsconfig.json         # TypeScript 配置
└── package.json          # 项目依赖
```

## 快速开始

### 1. 安装依赖

```bash
cd baby-picture-app
npm install
```

### 2. 开发模式

#### H5 开发
```bash
npm run dev:h5
```

#### 微信小程序开发
```bash
npm run dev:mp-weixin
```

#### App 开发
```bash
npm run dev:app
```

### 3. 生产构建

#### H5 构建
```bash
npm run build:h5
```

#### 微信小程序构建
```bash
npm run build:mp-weixin
```

#### App 构建
```bash
npm run build:app
```

## 开发说明

### 使用 HBuilderX 开发

1. 打开 HBuilderX
2. 文件 → 打开目录 → 选择 `baby-picture-app` 文件夹
3. 运行 → 运行到浏览器/模拟器/真机

### 使用 CLI 开发

```bash
# H5 开发
npm run dev:h5

# 微信小程序开发（需要打开微信开发者工具导入项目）
npm run dev:mp-weixin
```

## API 配置

后端接口地址配置在 `src/utils/request.ts` 中：

```typescript
const BASE_URL = import.meta.env.DEV ? '/api' : 'http://localhost:9003'
```

开发环境通过 Vite 代理转发到后端：

```typescript
// vite.config.ts
proxy: {
  '/api': {
    target: 'http://localhost:9003',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

## 后端服务

确保后端服务已启动：

- 后端地址: http://localhost:9003
- MySQL 数据库: 确保已启动并初始化
- MinIO 对象存储: 确保已启动（用于文件上传）

## 功能开发进度

### 第一阶段 ✅ 已完成
- 用户登录/注册功能
- 基础项目结构搭建
- 状态管理配置

### 第二阶段 ✅ 已完成
- 照片视频列表展示
- 照片视频上传（拍照/相册选择）
- 照片预览功能
- 文件删除功能

### 第三阶段 🔄 进行中
- 日记本功能
- 个人中心页面

## 注意事项

1. **TabBar 图标**: 需要在 `static/` 目录下添加对应的 tabBar 图标文件
2. **权限配置**: 微信小程序需要在 `manifest.json` 中配置相关权限
3. **HTTPS**: 生产环境需要使用 HTTPS 协议

## License

MIT
