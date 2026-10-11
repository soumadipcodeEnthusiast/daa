import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Badge, Card, SectionHeading, Skeleton } from '../components/ui'
import { useToast } from '../context/ToastContext'
import type { AlgorithmMeta } from '../types'

export function TheoryCards() {
  const { push } = useToast()
  const [algorithms, setAlgorithms] = useState<AlgorithmMeta[] | null>(null)

  useEffect(() => {
    api
      .getAlgorithms()
      .then(setAlgorithms)
      .catch(() => push('Could not load algorithm metadata', 'error'))
  }, [push])

  return (
    <div>
      <SectionHeading
        title="Theory Cards"
        description="Complexity, stability, and the key theorem behind every algorithm in this build."
      />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {!algorithms &&
          Array.from({ length: 6 }).map((_, i) => <Skeleton key={i} className="h-72" />)}

        {algorithms?.map((a) => (
          <Card key={a.id}>
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-lg font-semibold text-neutral-900">{a.name}</h3>
              <Badge tone="accent">{a.category === 'comparison' ? 'Comparison-based' : 'Non-comparison'}</Badge>
            </div>

            <table className="w-full text-sm mb-3">
              <tbody>
                <Row label="Best case" value={a.bestCase} />
                <Row label="Average case" value={a.averageCase} />
                <Row label="Worst case" value={a.worstCase} />
                <Row label="Space" value={a.spaceComplexity} />
              </tbody>
            </table>

            <div className="flex gap-2 mb-3">
              <Badge tone={a.stable ? 'good' : 'bad'}>{a.stable ? 'Stable' : 'Not stable'}</Badge>
              <Badge tone={a.inPlace ? 'good' : 'bad'}>{a.inPlace ? 'In-place' : 'Needs extra memory'}</Badge>
            </div>

            <div className="rounded-lg bg-neutral-100 border border-neutral-200 p-3 font-mono text-xs text-purple-700 font-semibold mb-3 overflow-x-auto whitespace-pre-wrap">
              {a.recurrence}
            </div>

            <p className="text-sm text-neutral-600 leading-relaxed">{a.theoremNote}</p>
          </Card>
        ))}
      </div>
    </div>
  )
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <tr className="border-b border-arena-border">
      <td className="py-1.5 pr-3 text-neutral-500 w-32 font-medium">{label}</td>
      <td className="py-1.5 font-mono text-neutral-800 font-medium">{value}</td>
    </tr>
  )
}
