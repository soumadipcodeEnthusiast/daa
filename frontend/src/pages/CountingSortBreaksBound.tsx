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
import { Button, Card, SectionHeading, Skeleton } from '../components/ui'
import { useGuidedDemo } from '../context/GuidedDemoContext'
import { useToast } from '../context/ToastContext'
import { colorForIndex } from '../lib/palette'
import type { BenchmarkSeries } from '../types'

const SCALE_SIZES = [1000, 5000, 10000, 50000, 100000, 500000, 1000000]
const SCALE_ALGOS = ['counting', 'radix', 'merge']
const FIXED_N_FOR_KSLIDER = 50000
const K_EXPONENTS = [2, 3, 4, 5, 6, 7] // k = 10^exponent

export function CountingSortBreaksBound() {
  const { push } = useToast()
  const [scaleLoading, setScaleLoading] = useState(false)
  const [scaleSeries, setScaleSeries] = useState<BenchmarkSeries[] | null>(null)

  const [kExponentIdx, setKExponentIdx] = useState(3)
  const [kLoading, setKLoading] = useState(false)
  const [kResults, setKResults] = useState<{ k: number; medianNanos: number; extraMemoryUnits: number }[]>([])

  const { active: guidedActive, stepIndex: guidedStepIndex, steps: guidedSteps } = useGuidedDemo()
  const thisStepIndex = guidedSteps.findIndex((s) => s.route === '/counting-breaks-bound')

  async function runScale() {
    setScaleLoading(true)
    try {
      setScaleSeries(await api.benchmark(SCALE_ALGOS, 'random', SCALE_SIZES, 5, 2000))
    } catch (e) {
      push(e instanceof Error ? e.message : 'Benchmark failed', 'error')
    } finally {
      setScaleLoading(false)
    }
  }

  useEffect(() => {
    if (guidedActive && guidedStepIndex === thisStepIndex) {
      runScale()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [guidedActive, guidedStepIndex])

  async function runForK(exponentIdx: number) {
    setKExponentIdx(exponentIdx)
    const k = Math.pow(10, K_EXPONENTS[exponentIdx])
    setKLoading(true)
    try {
      const result = await api.benchmark(['counting'], 'random', [FIXED_N_FOR_KSLIDER], 3, k)
      const point = result[0].points[0]
      setKResults((prev) => {
        const next = prev.filter((r) => r.k !== k)
        next.push({ k, medianNanos: point.medianNanos, extraMemoryUnits: point.extraMemoryUnits })
        return next.sort((a, b) => a.k - b.k)
      })
    } catch (e) {
      push(e instanceof Error ? e.message : 'Benchmark failed', 'error')
    } finally {
      setKLoading(false)
    }
  }

  const scaleData = useMemo(() => {
    if (!scaleSeries) return []
    return SCALE_SIZES.map((n) => {
      const row: Record<string, number> = { size: n }
      for (const s of scaleSeries) {
        const point = s.points.find((p) => p.size === n)
        if (point && !point.skipped) row[s.algorithm] = point.medianNanos
      }
      return row
    })
  }, [scaleSeries])

  const currentK = Math.pow(10, K_EXPONENTS[kExponentIdx])

  return (
    <div>
      <SectionHeading
        title="Counting Sort Breaks the Bound"
        description="Non-comparison sorts aren't bound by Omega(n log n) -- but they trade that for an O(n + k) dependence on the key range."
      />

      <Card className="mb-6" title="Linear time at scale" subtitle="Key range fixed at k=2,000 -- watch counting/radix pull away from merge sort as n grows to 1,000,000.">
        <Button onClick={runScale} disabled={scaleLoading} className="mb-4">
          {scaleLoading ? 'Running…' : 'Run up to n = 1,000,000'}
        </Button>

        {scaleLoading && <Skeleton className="h-80" />}

        {scaleSeries && !scaleLoading && (
          <ResponsiveContainer width="100%" height={340}>
            <LineChart data={scaleData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
              <CartesianGrid stroke="#e5e5e5" strokeDasharray="3 3" />
              <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
              <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} tickFormatter={(v) => `${(v / 1e6).toFixed(1)}ms`} />
              <RechartsTooltip
                contentStyle={{ background: '#ffffff', border: '1px solid #e0e0e0', borderRadius: 8, color: '#171717', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)' }}
                formatter={(value) => `${(Number(value) / 1e6).toFixed(3)} ms`}
              />
              <RechartsLegend />
              {SCALE_ALGOS.map((alg, idx) => (
                <Line key={alg} dataKey={alg} name={alg} stroke={colorForIndex(idx)} strokeWidth={2} dot={false} connectNulls />
              ))}
            </LineChart>
          </ResponsiveContainer>
        )}
      </Card>

      <Card title="The O(n + k) tradeoff" subtitle={`Counting sort at a fixed n = ${FIXED_N_FOR_KSLIDER.toLocaleString()} -- slide the key range and watch time and memory grow with k.`}>
        <div className="mb-4">
          <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
            Key range k = 10<sup>{K_EXPONENTS[kExponentIdx]}</sup> = {currentK.toLocaleString()}
          </label>
          <input
            type="range"
            min={0}
            max={K_EXPONENTS.length - 1}
            step={1}
            value={kExponentIdx}
            onChange={(e) => runForK(Number(e.target.value))}
            disabled={kLoading}
            className="mt-3 w-full"
          />
          <div className="flex justify-between text-[10px] text-neutral-500 mt-1 font-mono font-medium">
            {K_EXPONENTS.map((e) => (
              <span key={e}>10^{e}</span>
            ))}
          </div>
        </div>

        {kLoading && <Skeleton className="h-64" />}

        {!kLoading && kResults.length > 0 && (
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={kResults} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
              <CartesianGrid stroke="#e5e5e5" strokeDasharray="3 3" />
              <XAxis dataKey="k" scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} tickFormatter={(v) => v.toLocaleString()} />
              <YAxis yAxisId="time" stroke="#737373" tick={{ fontSize: 11 }} tickFormatter={(v) => `${(v / 1e6).toFixed(1)}ms`} />
              <RechartsTooltip
                contentStyle={{ background: '#ffffff', border: '1px solid #e0e0e0', borderRadius: 8, color: '#171717', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)' }}
                formatter={(value, name) =>
                  name === 'medianNanos' ? [`${(Number(value) / 1e6).toFixed(2)} ms`, 'Time'] : [Number(value).toLocaleString(), 'Extra memory (ints)']
                }
              />
              <RechartsLegend />
              <Line yAxisId="time" dataKey="medianNanos" name="Time" stroke={colorForIndex(0)} strokeWidth={2} dot />
            </LineChart>
          </ResponsiveContainer>
        )}

        {!kLoading && kResults.length > 0 && (
          <div className="mt-4 overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-neutral-500 border-b border-arena-border">
                  <th className="py-2 pr-4 font-semibold">k</th>
                  <th className="py-2 pr-4 font-semibold">Median time</th>
                  <th className="py-2 pr-4 font-semibold">Extra memory (ints)</th>
                </tr>
              </thead>
              <tbody>
                {kResults.map((r) => (
                  <tr key={r.k} className="border-b border-arena-border">
                    <td className="py-2 pr-4 font-mono text-neutral-700">{r.k.toLocaleString()}</td>
                    <td className="py-2 pr-4 font-mono text-neutral-700">{(r.medianNanos / 1e6).toFixed(3)} ms</td>
                    <td className="py-2 pr-4 font-mono text-neutral-700">{r.extraMemoryUnits.toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {kResults.length === 0 && !kLoading && (
          <div className="text-center text-neutral-500 py-10 font-medium">Move the slider to measure counting sort at that key range.</div>
        )}
      </Card>
    </div>
  )
}
