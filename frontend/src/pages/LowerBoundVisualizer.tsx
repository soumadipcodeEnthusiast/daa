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
import { DecisionTreeDiagram } from '../components/DecisionTreeDiagram'
import { useGuidedDemo } from '../context/GuidedDemoContext'
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

  const { active: guidedActive, stepIndex: guidedStepIndex, steps: guidedSteps } = useGuidedDemo()
  const thisStepIndex = guidedSteps.findIndex((s) => s.route === '/lower-bound')

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

  useEffect(() => {
    if (guidedActive && guidedStepIndex === thisStepIndex) {
      run()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [guidedActive, guidedStepIndex])

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
            {loading ? 'Running…' : 'Measure comparisons for n = 4..2000'}
          </Button>

          {loading && <Skeleton className="h-80" />}

          {series && !loading && (
            <ResponsiveContainer width="100%" height={340}>
              <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid stroke="#e5e5e5" strokeDasharray="3 3" />
                <XAxis dataKey="size" scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
                <YAxis scale="log" domain={['auto', 'auto']} type="number" stroke="#737373" tick={{ fontSize: 11 }} />
                <RechartsTooltip
                  contentStyle={{ background: '#ffffff', border: '1px solid #e0e0e0', borderRadius: 8, color: '#171717', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.08)' }}
                />
                <RechartsLegend />
                {ALGORITHMS.map((alg, idx) => (
                  <Line key={alg} dataKey={alg} name={alg} stroke={colorForIndex(idx)} strokeWidth={2} dot={false} connectNulls />
                ))}
                <Line dataKey="log2Factorial" name="log2(n!)  [lower bound]" stroke="#7C3AED" strokeDasharray="6 2" strokeWidth={2} dot={false} />
                <Line dataKey="nlogn" name="n·log2(n)" stroke="#a3a3a3" strokeDasharray="2 2" strokeWidth={1.5} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          )}

          {!series && !loading && (
            <div className="text-center text-neutral-500 py-16 font-medium">Run the measurement to see real algorithms sit above the bound.</div>
          )}
        </Card>

        <Card title="Proof sketch">
          <ol className="text-sm text-neutral-700 space-y-3 list-decimal pl-4 leading-relaxed">
            <li>
              Model any comparison-based sort as a <span className="text-[#8C6D23] font-semibold">decision tree</span>: each
              internal node is one comparison, each leaf is one output permutation.
            </li>
            <li>
              Sorting n distinct elements must produce all <span className="font-mono text-[#7C3AED] font-semibold">n!</span>{' '}
              possible orderings, so the tree needs <span className="font-mono text-[#7C3AED] font-semibold">n!</span> leaves.
            </li>
            <li>
              A binary tree of height h has at most 2<sup>h</sup> leaves, so{' '}
              <span className="font-mono text-[#7C3AED] font-semibold">2^h &ge; n!</span>, i.e.{' '}
              <span className="font-mono text-[#7C3AED] font-semibold">h &ge; log2(n!)</span>.
            </li>
            <li>
              By Stirling's approximation, <span className="font-mono text-[#7C3AED] font-semibold">log2(n!) = Theta(n log n)</span>.
              The tree's height is a lower bound on worst-case comparisons, so every comparison sort is{' '}
              <span className="text-[#8C6D23] font-semibold">Omega(n log n)</span> in the worst case.
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
