# 家庭记忆系统 - 启动说明

## 功能一：登录注册功能 ✅ 已完成

### 验收步骤

#### 1. 准备数据库

确保 MySQL 已启动，然后执行数据库初始化脚本：

```bash
mysql -u root -p < baby-picture-backend/src/main/resources/schema.sql
```

或者在 MySQL 客户端中执行脚本内容。

#### 2. 启动后端

```bash
cd baby-picture-backend

# 方式一：使用 Maven
mvn spring-boot:run

# 方式二：使用 IDE (IntelliJ IDEA)
# 打开项目后，运行 BabyPictureBackendApplication.java
```

后端服务地址：http://localhost:9003

#### 3. 启动前端

```bash
cd baby-picture-show
npm run dev
```

前端服务地址：http://localhost:5173

#### 4. 验收测试

1. 打开浏览器访问 http://localhost:5173
2. 应该看到温馨风格的登录页面
3. 点击"注册"标签，注册一个新用户：
   - 输入用户名（如：test）
   - 输入邮箱（如：test@example.com）
   - 输入密码（如：123456）
   - 确认密码
   - 点击"注册"
4. 注册成功后自动切换到登录页面
5. 使用刚注册的账号登录
6. 登录成功后跳转到首页（Dashboard）

### 已完成的文件

#### 前端
- `src/assets/warm.css` - 温馨主题样式
- `src/api/request.js` - Axios 封装
- `src/api/user.js` - 用户 API
- `src/router/index.js` - Vue Router 配置
- `src/components/Layout.vue` - 主布局组件
- `src/views/Login.vue` - 登录注册页面
- `src/views/Dashboard.vue` - 首页
- `src/views/MediaPage.vue` - 照片视频页面（占位）
- `src/views/DiaryPage.vue` - 日记本页面（占位）
- `src/views/ProfilePage.vue` - 个人中心页面

#### 后端
- `config/CorsConfig.java` - 跨域配置
- `response/LoginResponse.java` - 登录响应类
- 更新了 `controller/LoginController.java` - 登录注册接口

### 注意事项

1. 确保 MySQL 服务已启动
2. 确保数据库连接配置正确（application.properties）
3. 确保 MinIO 服务已启动（如需上传功能）
4. 后端默认端口：9003
5. 前端默认端口：5173

---

**下一个功能：照片视频管理**（待验收后开发）