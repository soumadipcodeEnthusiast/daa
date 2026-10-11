import { useEffect, useMemo, useState } from 'react'
import {
  CartesianGrid,
  Legend as RechartsLegend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip as RechartsTooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { api } from '../api/client'
import { Badge, Button, Card, SectionHeading, Skeleton } from '../components/ui'
import { useToast } from '../context/ToastContext'
import { colorForIndex } from '../lib/palette'
import { complexityLabels, scaleCurve } from '../lib/complexity'
import { INPUT_TYPES, type AlgorithmMeta, type BenchmarkRun, type BenchmarkSeries, type InputType } from '../types'

const PRESET_SIZES = [100, 500, 1000, 5000, 10000, 50000, 100000, 500000, 1000000]
const DEFAULT_SELECTION = ['bubble', 'insertion', 'merge', 'quick-random', 'heap', 'baseline']

export function BenchmarkLab() {
  const { push } = useToast()
  const [algorithms, setAlgorithms] = useState<AlgorithmMeta[]>([])
  const [selected, setSelected] = useState<string[]>(DEFAULT_SELECTION)
  const [inputType, setInputType] = useState<InputType>('random')
  const [sizes, setSizes] = useState<number[]>([100, 1000, 5000, 10000, 50000])
  const [repetitions, setRepetitions] = useState(5)
  const [loading, setLoading] = useState(false)
  const [series, setSeries] = useState<BenchmarkSeries[] | null>(null)
  const [historyOpen, setHistoryOpen] = useState(false)
  const [history, setHistory] = useState<BenchmarkRun[]>([])

  useEffect(() => {
    api
      .getAlgorithms()
      .then((all) => setAlgorithms(all.filter((a) => !a.id.endsWith('-search'))))
      .catch(() => push('Could not load algorithm list', 'error'))
  }, [push])

  function toggleAlgo(id: string) {
    setSelected((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  function toggleSize(size: number) {
    setSizes((prev) =>
      prev.includes(size) ? prev.filter((s) => s !== size) : [...prev, size].sort((a, b) => a - b)
    )
  }

  async function runBenchmark() {
    if (selected.length === 0 || sizes.length === 0) {
      push('Pick at least one algorithm and one size', 'error')
      return
    }
    setLoading(true)
    try {
      const result = await api.benchmark(selected, inputType, sizes, repetitions)
      setSeries(result)
    } catch (e) {
      push(e instanceof Error ? e.message : 'Benchmark failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  async function openHistory() {
    setHistoryOpen(true)
    try {
      setHistory(await api.history())
    } catch (e) {
      push(e instanceof Error ? e.message : 'Could not load history', 'error')
    }
  }

  const { timeData, compareData, legendKeys } = useMemo(() => {
    if (!series) return { timeData: [], compareData: [], legendKeys: [] as string[] }

    const allSizes = sizes.slice().sort((a, b) => a - b)
    const timeRows: Record<number, Record<string, number | null>> = {}
    const compareRows: Record<number, Record<string, number | null>> = {}
    allSizes.forEach((s) => {
      timeRows[s] = { size: s }
      compareRows[s] = { size: s }
    })

    for (const s of series) {
      for (const p of s.points) {
        timeRows[p.size][s.algorithm] = p.skipped ? null : p.medianNanos
        compareRows[p.size][s.algorithm] = p.skipped ? null : p.comparisons
      }
    }

    // Anchor theoretical curves to the largest non-skipped point across all series.
    let anchorSize = allSizes[allSizes.length - 1]
    let anchorTime = 1
    let anchorComparisons = 1
    outer: for (let i = allSizes.length - 1; i >= 0; i--) {
      const s = allSizes[i]
      for (const alg of series) {
        const point = alg.points.find((p) => p.size === s && !p.skipped)
        if (point) {
          anchorSize = s
          anchorTime = point.medianNanos
          anchorComparisons = point.comparisons
          break outer
        }
      }
    }

    const nCurveTime = scaleCurve('n', anchorSize, anchorTime)
    const nlognCurveTime = scaleCurve('nlogn', anchorSize, anchorTime)
    const n2CurveTime = scaleCurve('n2', anchorSize, anchorTime)
    const nCurveCmp = scaleCurve('n', anchorSize, anchorComparisons)
    const nlognCurveCmp = scaleCurve('nlogn', anchorSize, anchorComparisons)
    const n2CurveCmp = scaleCurve('n2', anchorSize, anchorComparisons)

    allSizes.forEach((s) => {
      timeRows[s].refN = nCurveTime(s)
      timeRows[s].refNLogN = nlognCurveTime(s)
      timeRows[s].refN2 = n2CurveTime(s)
      compareRows[s].refN = nCurveCmp(s)
      compareRows[s].refNLogN = nlognCurveCmp(s)
      compareRows[s].refN2 = n2CurveCmp(s)
    })

    return {
      timeData: allSizes.map((s) => timeRows[s]),
      compareData: allSizes.map((s) => compareRows[s]),
      legendKeys: series.map((s) => s.algorithm),
    }
  }, [series, sizes])

  const sortAlgos = algorithms.filter((a) => a.id !== 'linear-search' && a.id !== 'binary-search')

  return (
    <div>
      <SectionHeading
        title="Benchmark Lab"
        description="Real timing at large n: JIT warm-up, median of repetitions, log-log comparison against theoretical reference curves."
      />

      <Card className="mb-6">
        <div data-controls="true" className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">Algorithms</label>
            <div className="mt-2 flex flex-wrap gap-2">
              {sortAlgos.length === 0 &&
                Array.from({ length: 6 }).map((_, i) => <Skeleton key={i} className="h-8 w-24" />)}
              {sortAlgos.map((a) => {
                const active = selected.includes(a.id)
                return (
                  <button
                    key={a.id}
                    onClick={() => toggleAlgo(a.id)}
                    className={`rounded-lg border px-3 py-1.5 text-sm transition font-medium ${
                      active
                        ? 'border-[#C9A84C] bg-[#C9A84C]/15 text-[#8C6D23]'
                        : 'border-arena-border bg-white text-neutral-700 hover:text-black hover:border-neutral-400'
                    }`}
                  >
                    {a.name}
                  </button>
                )
              })}
            </div>
          </div>

          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">Sizes (n)</label>
            <div className="mt-2 flex flex-wrap gap-2">
              {PRESET_SIZES.map((s) => {
                const active = sizes.includes(s)
                return (
                  <button
                    key={s}
                    onClick={() => toggleSize(s)}
                    className={`rounded-lg border px-2.5 py-1 text-xs font-mono transition font-medium ${
                      active
                        ? 'border-[#7C3AED] bg-[#7C3AED]/15 text-[#6D28D9]'
                        : 'border-arena-border bg-white text-neutral-700 hover:text-black hover:border-neutral-400'
                    }`}
                  >
                    {s.toLocaleString()}
                  </button>
                )
              })}
            </div>
            <p className="mt-2 text-xs text-neutral-500">
              O(n^2) algorithms auto-skip above 20,000; everything auto-skips above 1,000,000.
            </p>
          </div>

          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">Input type</label>
            <select
              value={inputType}
              onChange={(e) => setInputType(e.target.value as InputType)}
              className="mt-2 w-full rounded-lg border border-arena-border bg-white px-3 py-2 text-sm text-neutral-900 shadow-sm focus:border-[#C9A84C] focus:outline-none"
            >
              {INPUT_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold mt-4 block">
              Repetitions: <span className="text-neutral-900 font-mono font-bold">{repetitions}</span>
            </label>
            <input
              type="range"
              min={1}
              max={11}
              step={2}
              value={repetitions}
              onChange={(e) => setRepetitions(Number(e.target.value))}
              className="mt-3 w-full"
            />
          </div>
        </div>

        <div className="mt-5 flex items-center gap-3">
          <Button onClick={runBenchmark} disabled={loading}>
            {loading ? 'Running…' : 'Run Benchmark'}
          </Button>
          <Button variant="secondary" onClick={openHistory}>
            History
          </Button>
        </div>
      </Card>

      {loading && <Skeleton className="h-96 mb-6" />}

      {series && !loading && (
        <>
          <Card className="mb-6" title="Time vs n (log-log)" subtitle="Dashed lines are theoretical references, anchored to the largest measured point.">
            <ResponsiveContainer width="100%" height={360}>
              <LineChart data={timeData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid stroke="#e5e5e5" strokeDasharray="3 3" />
                <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
                <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} tickFormatter={(v) => `${(v / 1e6).toFixed(1)}ms`} />
                <RechartsTooltip
                  contentStyle={{ background: '#ffffff', border: '1px solid #e0e0e0', borderRadius: 8, color: '#171717', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)' }}
                  formatter={(value, name) => [`${(Number(value) / 1e6).toFixed(3)} ms`, labelFor(String(name), algorithms)]}
                />
                <RechartsLegend formatter={(name: string) => labelFor(name, algorithms)} />
                {legendKeys.map((algId, idx) => (
                  <Line key={algId} dataKey={algId} stroke={colorForIndex(idx)} strokeWidth={2} dot={false} connectNulls={false} />
                ))}
                <Line dataKey="refN" stroke="#737373" strokeDasharray="4 3" strokeWidth={1.5} dot={false} name={complexityLabels.n} />
                <Line dataKey="refNLogN" stroke="#a3a3a3" strokeDasharray="2 2" strokeWidth={1.5} dot={false} name={complexityLabels.nlogn} />
                <Line dataKey="refN2" stroke="#d4d4d4" strokeDasharray="6 2" strokeWidth={1.5} dot={false} name={complexityLabels.n2} />
              </LineChart>
            </ResponsiveContainer>
          </Card>

          <Card className="mb-6" title="Comparisons vs n (log-log)">
            <ResponsiveContainer width="100%" height={360}>
              <LineChart data={compareData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid stroke="#e5e5e5" strokeDasharray="3 3" />
                <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
                <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
                <RechartsTooltip
                  contentStyle={{ background: '#ffffff', border: '1px solid #e0e0e0', borderRadius: 8, color: '#171717', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)' }}
                  formatter={(value, name) => [Number(value).toLocaleString(), labelFor(String(name), algorithms)]}
                />
                <RechartsLegend formatter={(name: string) => labelFor(name, algorithms)} />
                {legendKeys.map((algId, idx) => (
                  <Line key={algId} dataKey={algId} stroke={colorForIndex(idx)} strokeWidth={2} dot={false} connectNulls={false} />
                ))}
                <Line dataKey="refN" stroke="#737373" strokeDasharray="4 3" strokeWidth={1.5} dot={false} />
                <Line dataKey="refNLogN" stroke="#a3a3a3" strokeDasharray="2 2" strokeWidth={1.5} dot={false} />
                <Line dataKey="refN2" stroke="#d4d4d4" strokeDasharray="6 2" strokeWidth={1.5} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </Card>

          <Card title="Results table">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-neutral-500 border-b border-arena-border">
                    <th className="py-2 pr-4 font-semibold">Algorithm</th>
                    <th className="py-2 pr-4 font-semibold">n</th>
                    <th className="py-2 pr-4 font-semibold">Median time</th>
                    <th className="py-2 pr-4 font-semibold">Comparisons</th>
                    <th className="py-2 pr-4 font-semibold">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {series.flatMap((s) =>
                    s.points.map((p) => (
                      <tr key={`${s.algorithm}-${p.size}`} className="border-b border-arena-border">
                        <td className="py-2 pr-4 font-medium text-neutral-900">{labelFor(s.algorithm, algorithms)}</td>
                        <td className="py-2 pr-4 font-mono text-neutral-700">{p.size.toLocaleString()}</td>
                        <td className="py-2 pr-4 font-mono text-neutral-700">
                          {p.skipped ? '—' : `${(p.medianNanos / 1e6).toFixed(3)} ms`}
                        </td>
                        <td className="py-2 pr-4 font-mono text-neutral-700">{p.skipped ? '—' : p.comparisons.toLocaleString()}</td>
                        <td className="py-2 pr-4">
                          {p.skipped ? <Badge tone="bad">Skipped</Badge> : <Badge tone="good">Measured</Badge>}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </>
      )}

      {historyOpen && (
        <div className="fixed inset-0 z-40 flex justify-end bg-black/30 backdrop-blur-xs" onClick={() => setHistoryOpen(false)}>
          <div
            className="w-full max-w-md h-full bg-white border-l border-arena-border p-5 overflow-y-auto shadow-2xl"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-900">Benchmark history</h3>
              <Button variant="ghost" onClick={() => setHistoryOpen(false)}>Close</Button>
            </div>
            <div className="space-y-2">
              {history.length === 0 && <p className="text-neutral-500 text-sm">No runs recorded yet.</p>}
              {history.map((h) => (
                <div key={h.id} className="rounded-lg border border-arena-border bg-neutral-50 p-3 text-sm">
                  <div className="flex justify-between">
                    <span className="font-semibold text-neutral-900">{labelFor(h.algorithm, algorithms)}</span>
                    <span className="text-neutral-500 text-xs">{new Date(h.timestamp).toLocaleTimeString()}</span>
                  </div>
                  <div className="text-neutral-600 text-xs mt-1">
                    n={h.size.toLocaleString()} · {h.inputType} · {(h.medianNanos / 1e6).toFixed(3)} ms · {h.comparisons.toLocaleString()} comparisons
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function labelFor(id: string, algorithms: AlgorithmMeta[]) {
  if (id === 'refN' || id === 'n') return complexityLabels.n
  if (id === 'refNLogN' || id === 'nlogn') return complexityLabels.nlogn
  if (id === 'refN2' || id === 'n2') return complexityLabels.n2
  return algorithms.find((a) => a.id === id)?.name ?? id
}
