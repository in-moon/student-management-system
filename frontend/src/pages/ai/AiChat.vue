<template>
  <div class="ai-chat-container">
    <el-card class="chat-header">
      <div class="header-content">
        <div>
          <h2 style="margin:0">🤖 AI 智能助手</h2>
          <p style="margin:4px 0 0;color:#909399;font-size:13px">
            基于大语言模型，帮助学生管理、课程咨询、成绩分析等
          </p>
        </div>
        <div>
          <el-button size="small" @click="loadHistory" :loading="loadingHistory">加载历史</el-button>
          <el-button size="small" type="danger" plain @click="handleClearHistory">清空历史</el-button>
          <el-button size="small" circle @click="showConfigDialog = true" title="AI 配置">
            <el-icon><Setting /></el-icon>
          </el-button>
        </div>
      </div>
    </el-card>

    <div class="chat-messages" ref="messagesContainer">
      <div v-if="messages.length === 0" class="chat-welcome">
        <div class="welcome-icon">💬</div>
        <p>我是学生管理系统的AI助手</p>
        <p class="hint">您可以问我关于学生管理、课程安排、成绩分析等问题</p>
        <div class="quick-questions">
          <el-tag v-for="q in quickQuestions" :key="q" @click="sendQuick(q)" class="quick-tag">
            {{ q }}
          </el-tag>
        </div>
      </div>

      <div v-for="(msg, idx) in messages" :key="idx" :class="['message-row', msg.role]">
        <div :class="['message-bubble', msg.role]">
          <div class="message-avatar">
            {{ msg.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
      </div>

      <div v-if="streaming" class="message-row assistant">
        <div class="message-bubble assistant">
          <div class="message-avatar">🤖</div>
          <div class="message-content">
            <div class="message-text">
              {{ streamingText }}
              <span class="cursor-blink">|</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="2"
        placeholder="输入您的问题，按 Enter 发送（Shift+Enter 换行）"
        :disabled="streaming"
        @keydown.enter.exact="handleSend"
        resize="none"
      />
      <el-button
        type="primary"
        :disabled="!inputText.trim() || streaming"
        @click="handleSend"
        :loading="streaming"
        style="margin-left:12px;height:52px;width:80px"
      >
        {{ streaming ? '思考中' : '发送' }}
      </el-button>
    </div>

    <!-- AI 配置弹窗 -->
    <el-dialog v-model="showConfigDialog" title="⚙️ AI 配置" width="500px" :close-on-click-modal="false">
      <el-form :model="configForm" label-width="100px">
        <el-form-item label="API Key">
          <el-input v-model="configForm.apiKey" type="password" show-password placeholder="sk-..." clearable />
          <div style="font-size:12px;color:#909399;margin-top:4px">
            支持 OpenAI / DeepSeek / 通义千问 等兼容接口
          </div>
        </el-form-item>
        <el-form-item label="接口地址">
          <el-input v-model="configForm.baseUrl" placeholder="https://api.openai.com" clearable />
          <div style="font-size:12px;color:#909399;margin-top:4px">
            默认为 OpenAI 官方地址，可替换为国产模型代理地址
          </div>
        </el-form-item>
        <el-form-item label="模型名称">
          <el-select v-model="configForm.model" placeholder="选择模型" style="width:100%" filterable allow-create>
            <el-option label="gpt-3.5-turbo" value="gpt-3.5-turbo" />
            <el-option label="gpt-4o" value="gpt-4o" />
            <el-option label="gpt-4o-mini" value="gpt-4o-mini" />
            <el-option label="deepseek-chat" value="deepseek-chat" />
            <el-option label="deepseek-reasoner" value="deepseek-reasoner" />
            <el-option label="qwen-turbo" value="qwen-turbo" />
            <el-option label="qwen-plus" value="qwen-plus" />
            <el-option label="glm-4" value="glm-4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showConfigDialog = false">取消</el-button>
        <el-button type="danger" plain @click="handleDeleteConfig" :disabled="!configForm.hasConfig">
          恢复默认
        </el-button>
        <el-button type="primary" @click="handleSaveConfig" :loading="savingConfig">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'
import { chatWithAI, getChatHistory, clearChatHistory, getAiConfig, saveAiConfig, deleteAiConfig, type ChatMessage } from '@/api/ai'

interface DisplayMessage {
  role: 'user' | 'assistant'
  content: string
  time: string
}

const inputText = ref('')
const messages = ref<DisplayMessage[]>([])
const streaming = ref(false)
const streamingText = ref('')
const loadingHistory = ref(false)
const messagesContainer = ref<HTMLElement>()

const quickQuestions = [
  '学生成绩分析',
  '课程推荐建议',
  '系统功能介绍',
  '如何管理班级'
]

function scrollToBottom() {
  nextTick(() => {
    const el = messagesContainer.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function sendQuick(q: string) {
  inputText.value = q
  handleSend()
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || streaming.value) return

  const now = new Date().toLocaleTimeString()
  messages.value.push({ role: 'user', content: text, time: now })
  inputText.value = ''
  scrollToBottom()

  streaming.value = true
  streamingText.value = ''

  const history: ChatMessage[] = messages.value.map(m => ({
    role: m.role,
    content: m.content
  }))

  chatWithAI(
    { message: text, history },
    (chunk) => {
      streamingText.value += chunk
      scrollToBottom()
    },
    () => {
      if (streamingText.value) {
        messages.value.push({
          role: 'assistant',
          content: streamingText.value,
          time: new Date().toLocaleTimeString()
        })
      }
      streamingText.value = ''
      streaming.value = false
      scrollToBottom()
    },
    (err) => {
      ElMessage.error('AI助手暂时无法响应: ' + err)
      streaming.value = false
      streamingText.value = ''
    }
  )
}

async function loadHistory() {
  loadingHistory.value = true
  try {
    const records = await getChatHistory()
    messages.value = records.map(r => ({
      role: r.role as 'user' | 'assistant',
      content: r.content,
      time: r.createdAt ? new Date(r.createdAt).toLocaleTimeString() : ''
    }))
    scrollToBottom()
    ElMessage.success(`加载了 ${records.length} 条历史记录`)
  } catch {
    ElMessage.warning('加载历史记录失败')
  } finally {
    loadingHistory.value = false
  }
}

async function handleClearHistory() {
  try {
    await ElMessageBox.confirm('确定要清空所有对话历史吗？', '确认', { type: 'warning' })
    await clearChatHistory()
    messages.value = []
    ElMessage.success('历史已清空')
  } catch {}
}

// AI 配置
const showConfigDialog = ref(false)
const savingConfig = ref(false)
const configForm = ref({ apiKey: '', baseUrl: '', model: 'gpt-3.5-turbo', hasConfig: false })

async function loadConfig() {
  try {
    const res = await getAiConfig()
    configForm.value = { ...res.data }
  } catch {}
}

async function handleSaveConfig() {
  savingConfig.value = true
  try {
    await saveAiConfig({
      apiKey: configForm.value.apiKey,
      baseUrl: configForm.value.baseUrl,
      model: configForm.value.model
    })
    ElMessage.success('AI 配置已保存，下次对话生效')
    showConfigDialog.value = false
    await loadConfig()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    savingConfig.value = false
  }
}

async function handleDeleteConfig() {
  try {
    await ElMessageBox.confirm('确定恢复默认配置吗？', '确认', { type: 'warning' })
    await deleteAiConfig()
    configForm.value = { apiKey: '', baseUrl: '', model: 'gpt-3.5-turbo', hasConfig: false }
    ElMessage.success('已恢复默认配置')
    showConfigDialog.value = false
  } catch {}
}

onMounted(() => { scrollToBottom(); loadConfig() })
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  max-width: 900px;
  margin: 0 auto;
}

.chat-header {
  margin-bottom: 12px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #e4e7ed;
}

.chat-welcome {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.welcome-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.hint {
  font-size: 13px;
  color: #c0c4cc;
}

.quick-questions {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.message-row {
  margin-bottom: 16px;
}

.message-bubble {
  display: flex;
  gap: 10px;
}

.message-bubble.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.message-content {
  max-width: 75%;
}

.message-bubble.user .message-content {
  text-align: right;
}

.message-text {
  padding: 10px 14px;
  border-radius: 12px;
  background: #f0f2f5;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
}

.message-bubble.user .message-text {
  background: #409eff;
  color: #fff;
}

.message-time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
}

.cursor-blink {
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}

.chat-input-area {
  display: flex;
  align-items: flex-end;
}
</style>
