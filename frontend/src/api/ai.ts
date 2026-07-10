import http from './http'

export interface ChatMessage {
  role: 'user' | 'assistant' | 'system'
  content: string
}

export interface ChatRequest {
  message: string
  history?: ChatMessage[]
}

export interface ConversationRecord {
  id: number
  userId: number
  role: string
  content: string
  model: string
  createdAt: string
}

// 流式AI对话
export function chatWithAI(request: ChatRequest, onMessage: (text: string) => void, onDone: () => void, onError: (err: string) => void): AbortController {
  const controller = new AbortController()

  fetch('/api/ai/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify(request),
    signal: controller.signal
  }).then(async response => {
    if (!response.ok) {
      onError('AI服务请求失败: ' + response.status)
      return
    }
    const reader = response.body?.getReader()
    if (!reader) {
      onError('无法读取流式响应')
      return
    }
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('event:done')) {
          onDone()
          return
        }
        if (line.startsWith('event:error')) {
          onError('AI服务返回错误')
          return
        }
        if (line.startsWith('data:')) {
          const data = line.substring(5).trim()
          if (data && data !== '[DONE]') {
            onMessage(data)
          }
        }
      }
    }
    onDone()
  }).catch(err => {
    if (err.name !== 'AbortError') {
      onError('网络错误: ' + err.message)
    }
  })

  return controller
}

// 获取对话历史
export function getChatHistory(): Promise<ConversationRecord[]> {
  return http.get('/ai/history').then(res => res.data)
}

// 清空对话历史
export function clearChatHistory(): Promise<void> {
  return http.delete('/ai/history')
}

// AI 配置
export interface AiUserConfig {
  apiKey: string
  baseUrl: string
  model: string
  hasConfig: boolean
}

export function getAiConfig(): Promise<{ data: AiUserConfig }> {
  return http.get('/ai/config')
}

export function saveAiConfig(config: { apiKey: string; baseUrl: string; model: string }): Promise<{ data: { success: boolean; message: string } }> {
  return http.put('/ai/config', config)
}

export function deleteAiConfig(): Promise<{ data: { success: boolean; message: string } }> {
  return http.delete('/ai/config')
}
