import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export class ApiError extends Error {
  constructor(status, message) {
    super(message)
    this.status = status
  }
}

// 백엔드 공용 fetch 래퍼 — auth 스토어의 토큰을 자동으로 실어 보내고,
// 401(로그인 필요)/403(권한 없음)을 프론트 라우터 가드와 같은 방식으로 처리한다.
// useAuthStore()를 함수 안에서만 호출해서 auth.js와의 순환 import를 안전하게 피한다.
export async function apiFetch(path, { method = 'GET', body } = {}) {
  const auth = useAuthStore()
  const headers = { 'Content-Type': 'application/json' }
  if (auth.token) headers.Authorization = `Bearer ${auth.token}`

  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401) {
    auth.logout()
    router.push({ name: 'login' })
    throw new ApiError(401, '로그인이 필요해요.')
  }
  if (response.status === 403) {
    router.push({ name: 'forbidden' })
    throw new ApiError(403, '권한이 없어요.')
  }

  if (!response.ok) {
    const payload = await response.json().catch(() => null)
    throw new ApiError(response.status, payload?.message ?? '요청에 실패했어요.')
  }

  const text = await response.text()
  return text ? JSON.parse(text) : null
}
