import { useMemo, useState } from 'react'
import { api } from '../api/client'
import { Badge, Button, Card, SectionHeading, Skeleton } from '../components/ui'
import { useSearchEngine, type SearchPanelState } from '../hooks/useSearchEngine'
import { useToast } from '../context/ToastContext'
import type { Step } from '../types'

function generateSortedArray(size: number, seed: number) {
  let s = seed
  const rnd = () => {
    s = (s * 1103515245 + 12345) & 0x7fffffff
    return s / 0x7fffffff
  }
  const values = new Set<number>()
  while (values.size < size) values.add(Math.floor(rnd() * size * 4))
  return Array.from(values).sort((a, b) => a - b)
}

export function SearchVisualizer() {
  const { push } = useToast()
  const [size, setSize] = useState(24)
  const [seed, setSeed] = useState(7)
  const [array, setArray] = useState<number[]>(() => generateSortedArray(24, 7))
  const [target, setTarget] = useState<number>(array[Math.floor(array.length / 2)] ?? 0)
  const [speedMs, setSpeedMs] = useState(300)
  const [loading, setLoading] = useState(false)
  const [stepsByAlgorithm, setStepsByAlgorithm] = useState<Record<string, Step[]> | null>(null)

  const engine = useSearchEngine(stepsByAlgorithm ?? {}, speedMs)

  function regenerate(presentTarget: boolean) {
    const newSeed = seed + 1
    const arr = generateSortedArray(size, newSeed)
    setSeed(newSeed)
    setArray(arr)
    setTarget(presentTarget ? arr[Math.floor(Math.random() * arr.length)] : arr[arr.length - 1] + 1 + Math.floor(Math.random() * 50))
    setStepsByAlgorithm(null)
  }

  async function run() {
    setLoading(true)
    try {
      const [linear, binary] = await Promise.all([
        api.search('linear-search', array, target),
        api.search('binary-search', array, target),
      ])
      setStepsByAlgorithm({ 'linear-search': linear.steps, 'binary-search': binary.steps })
    } catch (e) {
      push(e instanceof Error ? e.message : 'Search failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  const maxValue = useMemo(() => Math.max(1, ...array), [array])

  return (
    <div>
      <SectionHeading
        title="Search Visualizer"
        description="Linear scan vs. halving the live range -- watch the comparison counts diverge."
      />

      <Card className="mb-6">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <div>
            <label className="text-xs uppercase tracking-wide text-slate-500 font-medium">
              Size: <span className="text-slate-200 font-mono">{size}</span>
            </label>
            <input
              type="range"
              min={10}
              max={100}
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value))
              }}
              className="mt-3 w-full"
            />
          </div>
          <div>
            <label className="text-xs uppercase tracking-wide text-slate-500 font-medium">Target</label>
            <input
              type="number"
              value={target}
              onChange={(e) => setTarget(Number(e.target.value))}
              className="mt-2 w-full rounded-lg border border-arena-border bg-arena-panel2 px-3 py-2 text-sm text-slate-200"
            />
          </div>
          <div>
            <label className="text-xs uppercase tracking-wide text-slate-500 font-medium">
              Speed: <span className="text-slate-200 font-mono">{speedMs}ms/step</span>
            </label>
            <input
              type="range"
              min={50}
              max={800}
              value={speedMs}
              onChange={(e) => setSpeedMs(Number(e.target.value))}
              className="mt-3 w-full"
            />
          </div>
          <div className="flex flex-col gap-2 justify-end">
            <Button variant="secondary" onClick={() => regenerate(true)}>
              🎲 New array (target present)
            </Button>
            <Button variant="secondary" onClick={() => regenerate(false)}>
              🎲 New array (target absent)
            </Button>
          </div>
        </div>

        <div className="mt-5 flex items-center gap-3">
          <Button onClick={run} disabled={loading}>
            {loading ? 'Searching…' : '🔍 Run Search'}
          </Button>
          {stepsByAlgorithm && (
            <>
              <Button variant="secondary" onClick={engine.playing ? engine.pause : engine.play} disabled={engine.allFinished}>
                {engine.playing ? '⏸ Pause' : '▶ Play'}
              </Button>
              <Button variant="secondary" onClick={engine.step} disabled={engine.playing || engine.allFinished}>
                ⏭ Step
              </Button>
              <Button variant="secondary" onClick={engine.reset}>
                ↺ Reset
              </Button>
            </>
          )}
        </div>
      </Card>

      {loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Skeleton className="h-56" />
          <Skeleton className="h-56" />
        </div>
      )}

      {stepsByAlgorithm && !loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {engine.panels.map((panel) => (
            <SearchPanel key={panel.algorithm} state={panel} array={array} maxValue={maxValue} />
          ))}
        </div>
      )}
    </div>
  )
}

const NAMES: Record<string, string> = { 'linear-search': 'Linear Search', 'binary-search': 'Binary Search' }

function SearchPanel({ state, array, maxValue }: { state: SearchPanelState; array: number[]; maxValue: number }) {
  return (
    <div className="rounded-xl border border-arena-border bg-arena-panel2/60 p-4 flex flex-col gap-3">
      <div className="flex items-center justify-between">
        <span className="font-semibold text-slate-100">{NAMES[state.algorithm] ?? state.algorithm}</span>
        {state.foundIndex !== null && <Badge tone="good">Found at index {state.foundIndex}</Badge>}
        {state.exhausted && <Badge tone="bad">Not found</Badge>}
      </div>

      <div className="h-36 flex items-end gap-[2px] bg-black/20 rounded-lg p-1.5 overflow-hidden">
        {array.map((value, idx) => {
          const inRange = state.rangeLo !== null && idx >= state.rangeLo && idx <= (state.rangeHi ?? -1)
          const isMid = idx === state.rangeMid
          const isCompare = idx === state.compareIndex
          const isFound = idx === state.foundIndex
          let colorClass = 'bg-slate-700'
          if (state.algorithm === 'binary-search' && inRange) colorClass = 'bg-slate-500'
          if (isMid) colorClass = 'bg-arena-pivot'
          if (isCompare) colorClass = 'bg-arena-compare'
          if (isFound) colorClass = 'bg-arena-sorted'
          const heightPct = Math.max(4, (value / maxValue) * 100)
          return (
            <div
              key={idx}
              className={`flex-1 rounded-t-sm transition-colors duration-100 ${colorClass}`}
              style={{ height: `${heightPct}%` }}
            />
          )
        })}
      </div>

      <div className="grid grid-cols-2 gap-2 text-xs">
        <div className="rounded-lg bg-black/20 px-2 py-1.5 text-center">
          <div className="font-mono text-sm font-semibold text-slate-100">{state.comparisons}</div>
          <div className="text-[10px] uppercase tracking-wide text-slate-500">Comparisons</div>
        </div>
        <div className="rounded-lg bg-black/20 px-2 py-1.5 text-center">
          <div className="font-mono text-sm font-semibold text-slate-100">
            {state.pointer}/{state.totalSteps}
          </div>
          <div className="text-[10px] uppercase tracking-wide text-slate-500">Steps</div>
        </div>
      </div>
    </div>
  )
}
