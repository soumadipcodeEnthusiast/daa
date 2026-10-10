import { useMemo, useState } from 'react'
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
import { useToast } from '../context/ToastContext'
import { colorForIndex } from '../lib/palette'
import type { BenchmarkSeries } from '../types'

const SIZES = [100, 500, 1000, 2000, 5000, 8000, 12000, 16000, 20000]
const ALGORITHMS = ['quick-first', 'quick-random']

export function QuicksortWorstCase() {
  const { push } = useToast()
  const [loading, setLoading] = useState(false)
  const [series, setSeries] = useState<BenchmarkSeries[] | null>(null)

  async function run() {
    setLoading(true)
    try {
      // Sorted input is the adversarial case for a first-element pivot.
      setSeries(await api.benchmark(ALGORITHMS, 'sorted', SIZES, 5))
    } catch (e) {
      push(e instanceof Error ? e.message : 'Benchmark failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  const data = useMemo(() => {
    if (!series) return []
    return SIZES.map((n) => {
      const row: Record<string, number> = {
        size: n,
        exactWorstCase: (n * (n - 1)) / 2,
        expectedRandom: 1.39 * n * Math.log2(Math.max(n, 2)),
      }
      for (const s of series) {
        const point = s.points.find((p) => p.size === n)
        if (point && !point.skipped) row[s.algorithm] = point.comparisons
      }
      return row
    })
  }, [series])

  return (
    <div>
      <SectionHeading
        title="Quicksort Worst Case"
        description="Same algorithm, same input, one line of pivot-selection code -- the difference between O(n log n) and O(n^2)."
      />

      <Card className="mb-6">
        <p className="text-sm text-slate-400 mb-4">
          Both variants run on <span className="text-slate-200 font-medium">already-sorted</span> input of growing
          size. First-element pivot always picks the smallest remaining element, so every partition splits into
          sizes 0 and n-1 -- the textbook degenerate case. The dashed lines are not fitted to the data: they are the
          literal closed-form predictions{' '}
          <span className="font-mono text-cyan-300">n(n-1)/2</span> (exact worst case) and{' '}
          <span className="font-mono text-cyan-300">1.39 · n · log2(n)</span> (expected comparisons for a random
          pivot), plotted directly.
        </p>
        <Button onClick={run} disabled={loading}>
          {loading ? 'Running…' : '⚡ Run on sorted input, n up to 20,000'}
        </Button>
      </Card>

      {loading && <Skeleton className="h-96" />}

      {series && !loading && (
        <Card title="Comparisons vs n (log-log)">
          <ResponsiveContainer width="100%" height={380}>
            <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
              <CartesianGrid stroke="#232b40" strokeDasharray="3 3" />
              <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#64748b" tick={{ fontSize: 11 }} />
              <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#64748b" tick={{ fontSize: 11 }} />
              <RechartsTooltip
                contentStyle={{ background: '#121725', border: '1px solid #232b40', borderRadius: 8 }}
                formatter={(value) => Number(value).toLocaleString()}
              />
              <RechartsLegend />
              <Line dataKey="quick-first" name="Quicksort (first pivot)" stroke={colorForIndex(1)} strokeWidth={2} dot connectNulls />
              <Line dataKey="quick-random" name="Quicksort (random pivot)" stroke={colorForIndex(0)} strokeWidth={2} dot connectNulls />
              <Line dataKey="exactWorstCase" name="n(n-1)/2  [exact worst case]" stroke="#e66767" strokeDasharray="6 2" strokeWidth={1.5} dot={false} />
              <Line dataKey="expectedRandom" name="1.39·n·log2(n)  [expected]" stroke="#9ca3af" strokeDasharray="2 2" strokeWidth={1.5} dot={false} />
            </LineChart>
          </ResponsiveContainer>
          <p className="mt-3 text-xs text-slate-500">
            If the solid "first pivot" line sits almost exactly on the red dashed line, and the "random pivot" line
            sits almost exactly on the gray dashed line, the theory isn't just asymptotically right -- the constants
            match too.
          </p>
        </Card>
      )}
    </div>
  )
}
