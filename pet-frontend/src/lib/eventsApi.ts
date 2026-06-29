import type { DiagnosisHistoryEvent } from '../types/event'
import { getApiBaseUrl } from './apiBase'

interface DiagnosisLogPayload {
  id?: number
  type?: 'Eye' | 'Skin'
  score?: number
  createdAt?: string
  disease?: {
    koreanName?: string
    description?: string
    treatment?: string
    riskLevel?: string
  }
}

function mapDiagnosisLog(log: DiagnosisLogPayload): DiagnosisHistoryEvent {
  return {
    id: log.id ?? 0,
    prediction: log.disease?.koreanName ?? '',
    type: log.type === 'Skin' ? 'Skin' : 'Eye',
    confidence: Math.round((log.score ?? 0) * 100),
    riskLevel: log.disease?.riskLevel ?? '',
    timestamp: log.createdAt ?? '',
    description: log.disease?.description ?? '',
    treatment: log.disease?.treatment ?? '',
  }
}

function normalizeEventsPayload(data: unknown): DiagnosisHistoryEvent[] {
  if (!Array.isArray(data)) return []
  return data.map((item) => mapDiagnosisLog(item as DiagnosisLogPayload))
}

export async function fetchEvents(): Promise<DiagnosisHistoryEvent[]> {
  const base = getApiBaseUrl()
  if (!base) return []

  const token = localStorage.getItem('accessToken')
  const url = `${base}/diagnosis/events`

  const res = await fetch(url, {
    headers: {
      Accept: 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  })

  if (!res.ok) {
    throw new Error(`진단 히스토리를 불러오지 못했습니다. (${res.status})`)
  }

  const json: unknown = await res.json()
  return normalizeEventsPayload(json)
}
