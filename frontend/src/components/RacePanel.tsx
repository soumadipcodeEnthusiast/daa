import type { PanelState } from '../hooks/useRaceEngine'

const HIGHLIGHT_CLASS: Record<string, string> = {
  compare: 'bg-arena-compare',
  swap: 'bg-arena-swap',
  pivot: 'bg-arena-pivot',
}

export function RacePanel({
  state,
  displayName,
  maxValue,
}: {
  state: PanelState
  displayName: string
  maxValue: number
}) {
  const progressPct = state.totalSteps === 0 ? 100 : Math.round((state.pointer / state.totalSteps) * 100)

  return (
    <div className="rounded-xl border border-arena-border bg-white shadow-sm p-4 flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-neutral-900">{displayName}</span>
          {state.finishRank && (
            <span
              className={`rounded-full px-2 py-0.5 text-xs font-bold ${
                state.finishRank === 1
                  ? 'bg-[#C9A84C] text-black shadow-sm'
                  : state.finishRank === 2
                    ? 'bg-neutral-200 text-neutral-800'
                    : state.finishRank === 3
                      ? 'bg-purple-100 text-purple-800 border border-purple-200'
                      : 'bg-neutral-100 text-neutral-600'
              }`}
            >
              #{state.finishRank}
            </span>
          )}
        </div>
        <span className="text-xs text-neutral-500 font-medium">{progressPct}%</span>
      </div>

      <div className="h-36 flex items-end gap-[1px] bg-neutral-100 border border-neutral-200 rounded-lg p-1.5 overflow-hidden">
        {state.array.map((value, idx) => {
          const kind = state.highlight.get(idx)
          const isSorted = state.sorted.has(idx)
          const heightPct = Math.max(4, (value / maxValue) * 100)
          let colorClass = 'bg-neutral-300'
          if (isSorted) colorClass = 'bg-arena-sorted'
          if (kind) colorClass = HIGHLIGHT_CLASS[kind]
          return (
            <div
              key={idx}
              className={`flex-1 rounded-t-sm transition-colors duration-100 ${colorClass}`}
              style={{ height: `${heightPct}%` }}
            />
          )
        })}
      </div>

      <div className="grid grid-cols-3 gap-2 text-xs">
        <Stat label="Comparisons" value={state.comparisons} />
        <Stat label="Swaps/Writes" value={state.swapsWrites} />
        <Stat label="Steps" value={`${state.pointer}/${state.totalSteps}`} />
      </div>
    </div>
  )
}

function Stat({ label, value }: { label: string; value: number | string }) {
  return (
    <div className="rounded-lg bg-neutral-50 border border-neutral-200 px-2 py-1.5 text-center">
      <div className="font-mono text-sm font-semibold text-neutral-900">{value}</div>
      <div className="text-[10px] uppercase tracking-wide text-neutral-500 font-medium">{label}</div>
    </div>
  )
}
