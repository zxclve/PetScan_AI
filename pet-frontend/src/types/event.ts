export type EventSeverity = 'HIGH' | 'MEDIUM' | 'LOW' | string

export type PetEventType =
  | 'DISEASE_ALERT'
  | 'BEHAVIOR_ANOMALY'
  | string

export interface EventRecord {
  eventId: number
  siteName: string
  eventType: PetEventType
  timestamp: string
  snapshotUrl: string
  severity: EventSeverity
}
// 히스토리 페이지에 뿌릴 내용
export interface DiagnosisHistoryEvent {
  id: number
  prediction: string
  type: 'Eye' | 'Skin'
  confidence: number
  riskLevel: string
  timestamp: string
  description: string
  treatment: string
}
