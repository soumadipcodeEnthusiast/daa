import { createContext, useContext, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'

export interface GuidedStep {
  title: string
  route: string
  caption: string
}

export const GUIDED_STEPS: GuidedStep[] = [
  {
    title: '1. The race: O(n²) vs O(n log n)',
    route: '/',
    caption:
      "Bubble sort and merge sort, same random input, side by side. Watch the comparison counters: bubble's climbs much faster than merge's, and merge finishes first every time at this size.",
  },
  {
    title: '2. Why O(n log n) is a floor, not a convention',
    route: '/lower-bound',
    caption:
      'Any comparison-based sort is a decision tree with n! leaves, so its height -- worst-case comparisons -- is at least log2(n!), which is Theta(n log n). Merge, heap, and randomized quicksort all sit right at that measured floor.',
  },
  {
    title: '3. Counting sort breaks the bound',
    route: '/counting-breaks-bound',
    caption:
      "Counting and radix sort never compare elements, so that floor doesn't apply to them -- they run in O(n+k) and look linear here, right up until the key-range slider makes k the bottleneck instead of n.",
  },
  {
    title: '4. Quicksort: one line of code, two different complexities',
    route: '/quicksort-worst-case',
    caption:
      "Same algorithm, same sorted input. First-element pivot always splits 0/n-1 -- that's the textbook O(n²) adversarial case. Randomizing the pivot choice collapses it back to the expected O(n log n), and the measured comparisons land almost exactly on the closed-form predictions.",
  },
  {
    title: '5. Summary',
    route: 'summary',
    caption:
      'Comparison sorts are bound by Omega(n log n); non-comparison sorts trade that bound for a dependence on the key range; and an adversarial worst case is often a property of a design choice (pivot selection), not the algorithm itself.',
  },
]

interface GuidedDemoContextValue {
  active: boolean
  stepIndex: number
  steps: GuidedStep[]
  start: () => void
  next: () => void
  prev: () => void
  exit: () => void
}

const GuidedDemoContext = createContext<GuidedDemoContextValue | null>(null)

export function GuidedDemoProvider({ children }: { children: ReactNode }) {
  const [active, setActive] = useState(false)
  const [stepIndex, setStepIndex] = useState(0)
  const navigate = useNavigate()

  function goTo(idx: number) {
    setStepIndex(idx)
    const step = GUIDED_STEPS[idx]
    if (step.route !== 'summary') navigate(step.route)
  }

  return (
    <GuidedDemoContext.Provider
      value={{
        active,
        stepIndex,
        steps: GUIDED_STEPS,
        start: () => {
          setActive(true)
          goTo(0)
        },
        next: () => {
          if (stepIndex < GUIDED_STEPS.length - 1) goTo(stepIndex + 1)
        },
        prev: () => {
          if (stepIndex > 0) goTo(stepIndex - 1)
        },
        exit: () => setActive(false),
      }}
    >
      {children}
    </GuidedDemoContext.Provider>
  )
}

export function useGuidedDemo() {
  const ctx = useContext(GuidedDemoContext)
  if (!ctx) throw new Error('useGuidedDemo must be used within GuidedDemoProvider')
  return ctx
}
