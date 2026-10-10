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
import { DecisionTreeDiagram } from '../components/DecisionTreeDiagram'
import { useToast } from '../context/ToastContext'
import { colorForIndex } from '../lib/palette'
import type { BenchmarkSeries } from '../types'

const SIZES = [4, 8, 16, 32, 64, 128, 256, 512, 1024, 2000]
const ALGORITHMS = ['merge', 'heap', 'quick-random']

function log2Factorial(n: number) {
  let sum = 0
  for (let k = 2; k <= n; k++) sum += Math.log2(k)
  return sum
}

export function LowerBoundVisualizer() {
  const { push } = useToast()
  const [loading, setLoading] = useState(false)
  const [series, setSeries] = useState<BenchmarkSeries[] | null>(null)

  async function run() {
    setLoading(true)
    try {
      setSeries(await api.benchmark(ALGORITHMS, 'random', SIZES, 5))
    } catch (e) {
      push(e instanceof Error ? e.message : 'Benchmark failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  const data = useMemo(() => {
    if (!series) return []
    return SIZES.map((n) => {
      const row: Record<string, number> = { size: n, log2Factorial: log2Factorial(n), nlogn: n * Math.log2(Math.max(n, 2)) }
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
        title="Lower-Bound Visualizer"
        description="No comparison-based sort can beat Omega(n log n) in the worst case -- here's why, and here's the proof that real algorithms respect it."
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">
        <Card className="lg:col-span-2" title="Measured comparisons vs. the information-theoretic bound">
          <Button onClick={run} disabled={loading} className="mb-4">
            {loading ? 'Running…' : '📐 Measure comparisons for n = 4..2000'}
          </Button>

          {loading && <Skeleton className="h-80" />}

          {series && !loading && (
            <ResponsiveContainer width="100%" height={340}>
              <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid stroke="#232b40" strokeDasharray="3 3" />
                <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#64748b" tick={{ fontSize: 11 }} />
                <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#64748b" tick={{ fontSize: 11 }} />
                <RechartsTooltip contentStyle={{ background: '#121725', border: '1px solid #232b40', borderRadius: 8 }} />
                <RechartsLegend />
                {ALGORITHMS.map((alg, idx) => (
                  <Line key={alg} dataKey={alg} name={alg} stroke={colorForIndex(idx)} strokeWidth={2} dot={false} connectNulls />
                ))}
                <Line dataKey="log2Factorial" name="log2(n!)  [lower bound]" stroke="#e66767" strokeDasharray="6 2" strokeWidth={2} dot={false} />
                <Line dataKey="nlogn" name="n·log2(n)" stroke="#9ca3af" strokeDasharray="2 2" strokeWidth={1.5} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          )}

          {!series && !loading && (
            <div className="text-center text-slate-500 py-16">Run the measurement to see real algorithms sit above the bound.</div>
          )}
        </Card>

        <Card title="Proof sketch">
          <ol className="text-sm text-slate-300 space-y-3 list-decimal pl-4">
            <li>
              Model any comparison-based sort as a <span className="text-indigo-300">decision tree</span>: each
              internal node is one comparison, each leaf is one output permutation.
            </li>
            <li>
              Sorting n distinct elements must produce all <span className="font-mono text-cyan-300">n!</span>{' '}
              possible orderings, so the tree needs <span className="font-mono text-cyan-300">n!</span> leaves.
            </li>
            <li>
              A binary tree of height h has at most 2<sup>h</sup> leaves, so{' '}
              <span className="font-mono text-cyan-300">2^h &ge; n!</span>, i.e.{' '}
              <span className="font-mono text-cyan-300">h &ge; log2(n!)</span>.
            </li>
            <li>
              By Stirling's approximation, <span className="font-mono text-cyan-300">log2(n!) = Theta(n log n)</span>.
              The tree's height is a lower bound on worst-case comparisons, so every comparison sort is{' '}
              <span className="text-indigo-300">Omega(n log n)</span> in the worst case.
            </li>
            <li>
              Counting sort and radix sort escape this because they never compare two elements -- see the next page.
            </li>
          </ol>
        </Card>
      </div>

      <Card title="The decision tree for n = 3 (6 leaves, height 3)" subtitle="Watch it walk through all 6 permutations of (a, b, c).">
        <DecisionTreeDiagram />
      </Card>
    </div>
  )
}
