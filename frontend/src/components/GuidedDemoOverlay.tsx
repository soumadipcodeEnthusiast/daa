import { useGuidedDemo } from '../context/GuidedDemoContext'
import { Button } from './ui'

export function GuidedDemoOverlay() {
  const { active, stepIndex, steps, next, prev, exit } = useGuidedDemo()
  if (!active) return null
  const step = steps[stepIndex]
  const isSummary = step.route === 'summary'

  return (
    <>
      {isSummary && (
        <div className="fixed inset-0 z-40 bg-arena-bg/95 backdrop-blur-sm flex items-center justify-center p-10">
          <div className="max-w-2xl text-center">
            <h2 className="text-3xl font-bold text-white mb-6">Summary</h2>
            <ul className="text-left text-lg text-slate-200 space-y-3 list-disc pl-6">
              <li>O(n²) sorts (bubble, insertion) are fine for tiny inputs, disastrous at scale.</li>
              <li>
                O(n log n) sorts (merge, heap, randomized quicksort) sit at the comparison-sort floor --
                Omega(n log n) is provable via the decision-tree argument.
              </li>
              <li>
                Counting and radix sort escape that floor entirely by never comparing elements, at the cost
                of an O(n+k) dependence on the key range.
              </li>
              <li>
                Quicksort's worst case is a property of the pivot strategy, not the algorithm --
                randomization turns an O(n²) adversarial input into an O(n log n) expectation.
              </li>
              <li>Every claim on these slides was measured live against the theory, not just asserted.</li>
            </ul>
          </div>
        </div>
      )}
      <div className="fixed bottom-0 left-0 right-0 z-50 bg-arena-panel/95 backdrop-blur-sm border-t border-arena-border px-6 py-4 flex items-center gap-4">
        <div className="flex-1 min-w-0">
          <div className="text-xs uppercase tracking-wide text-indigo-400 font-semibold">
            Guided Demo · Step {stepIndex + 1}/{steps.length} · {step.title}
          </div>
          <div className="text-sm text-slate-200 mt-0.5">{step.caption}</div>
        </div>
        <Button variant="secondary" onClick={prev} disabled={stepIndex === 0}>
          ← Prev
        </Button>
        <Button variant="secondary" onClick={next} disabled={stepIndex === steps.length - 1}>
          Next →
        </Button>
        <Button variant="danger" onClick={exit}>
          Exit
        </Button>
      </div>
    </>
  )
}
