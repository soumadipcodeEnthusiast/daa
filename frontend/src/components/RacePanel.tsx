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
    <div className="rounded-xl border border-arena-border bg-arena-panel2/60 p-4 flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="font-semibold text-slate-100">{displayName}</span>
          {state.finishRank && (
            <span
              className={`rounded-full px-2 py-0.5 text-xs font-bold ${
                state.finishRank === 1
                  ? 'bg-yellow-500 text-yellow-950'
                  : state.finishRank === 2
                    ? 'bg-slate-300 text-slate-900'
                    : state.finishRank === 3
                      ? 'bg-amber-700 text-amber-100'
                      : 'bg-slate-700 text-slate-200'
              }`}
            >
              #{state.finishRank}
            </span>
          )}
        </div>
        <span className="text-xs text-slate-500">{progressPct}%</span>
      </div>

      <div className="h-36 flex items-end gap-[1px] bg-black/20 rounded-lg p-1.5 overflow-hidden">
        {state.array.map((value, idx) => {
          const kind = state.highlight.get(idx)
          const isSorted = state.sorted.has(idx)
          const heightPct = Math.max(4, (value / maxValue) * 100)
          let colorClass = 'bg-slate-600'
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
    <div className="rounded-lg bg-black/20 px-2 py-1.5 text-center">
      <div className="font-mono text-sm font-semibold text-slate-100">{value}</div>
      <div className="text-[10px] uppercase tracking-wide text-slate-500">{label}</div>
    </div>
  )
}
