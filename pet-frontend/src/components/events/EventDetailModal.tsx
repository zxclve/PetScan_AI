import type { DiagnosisHistoryEvent } from '../../types/event'
import { Modal } from '../ui/Modal'
import { SeverityBadge } from '../ui/SeverityBadge'

function formatType(type: DiagnosisHistoryEvent['type']): string {
  return type === 'Skin' ? '피부' : '안구'
}

function formatTime(iso: string): string {
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).format(d)
}

export function EventDetailModal({
  open,
  event,
  onClose,
}: {
  open: boolean
  event: DiagnosisHistoryEvent | null
  onClose: () => void
}) {
  if (!event) return null

  return (
    <Modal open={open} title={`진단 히스토리 #${event.id}`} onClose={onClose}>
      <div className="grid gap-3 sm:gap-4">
        <div className="rounded-[2rem] bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
          <dt className="text-xs font-semibold text-slate-500">진단명</dt>
          <dd className="mt-1 text-sm font-semibold text-slate-900 sm:text-base">{event.prediction}</dd>
        </div>

        <div className="grid gap-3 sm:grid-cols-2">
          <div className="rounded-[2rem] bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
            <dt className="text-xs font-semibold text-slate-500">진단 부위</dt>
            <dd className="mt-1 text-sm font-semibold text-slate-900 sm:text-base">
              {formatType(event.type)}
            </dd>
          </div>

          <div className="rounded-[2rem] bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
            <dt className="text-xs font-semibold text-slate-500">기록 시각</dt>
            <dd className="mt-1 text-sm font-semibold text-slate-900 sm:text-base">
              {formatTime(event.timestamp)}
            </dd>
          </div>
        </div>

        <div className="grid gap-3 sm:grid-cols-2">
          <div className="rounded-[2rem] bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
            <dt className="text-xs font-semibold text-slate-500">신뢰도</dt>
            <dd className="mt-1 text-sm font-semibold text-slate-900 sm:text-base">
              {event.confidence}%
            </dd>
          </div>

          <div className="rounded-xl bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
            <dt className="text-xs font-semibold text-slate-500">위험도</dt>
            <dd className="mt-2 flex flex-wrap items-center gap-2">
              <SeverityBadge severity={event.riskLevel} />
              <span className="text-xs text-muted sm:text-sm">{event.riskLevel}</span>
            </dd>
          </div>
        </div>

        <div className="rounded-xl bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
          <dt className="text-xs font-semibold text-slate-500">설명</dt>
          <dd className="mt-1 whitespace-pre-line text-xs text-slate-700 sm:text-sm">
            {event.description}
          </dd>
        </div>

        <div className="rounded-xl bg-app-bg p-3 sm:p-4 ring-1 ring-slate-200/70">
          <dt className="text-xs font-semibold text-slate-500">치료 및 조치</dt>
          <dd className="mt-1 whitespace-pre-line text-xs text-slate-700 sm:text-sm">
            {event.treatment}
          </dd>
        </div>
      </div>
    </Modal>
  )
}
