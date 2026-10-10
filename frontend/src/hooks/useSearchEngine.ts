import { useCallback, useEffect, useRef, useState } from 'react'
import type { Step } from '../types'

export interface SearchPanelState {
  algorithm: string
  pointer: number
  totalSteps: number
  comparisons: number
  compareIndex: number | null
  rangeLo: number | null
  rangeHi: number | null
  rangeMid: number | null
  foundIndex: number | null
  exhausted: boolean
  finished: boolean
}

function applyStep(state: SearchPanelState, step: Step) {
  state.compareIndex = null
  switch (step.type) {
    case 'range':
      state.rangeLo = step.i
      state.rangeHi = step.j
      state.rangeMid = step.value
      break
    case 'compare':
      state.comparisons++
      state.compareIndex = step.i
      break
    case 'found':
      state.foundIndex = step.i
      break
    case 'not-found':
      state.exhausted = true
      break
  }
  state.pointer++
}

export function useSearchEngine(
  stepsByAlgorithm: Record<string, Step[]>,
  speedMs: number
) {
  const [playing, setPlaying] = useState(false)
  const [, forceRender] = useState(0)
  const statesRef = useRef<SearchPanelState[]>([])
  const intervalRef = useRef<number | null>(null)

  const algorithms = Object.keys(stepsByAlgorithm)

  const init = useCallback(() => {
    statesRef.current = algorithms.map((alg) => ({
      algorithm: alg,
      pointer: 0,
      totalSteps: stepsByAlgorithm[alg].length,
      comparisons: 0,
      compareIndex: null,
      rangeLo: null,
      rangeHi: null,
      rangeMid: null,
      foundIndex: null,
      exhausted: false,
      finished: stepsByAlgorithm[alg].length === 0,
    }))
    setPlaying(false)
    forceRender((n) => n + 1)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(stepsByAlgorithm)])

  useEffect(() => {
    init()
  }, [init])

  const stepAll = useCallback(() => {
    let anyActive = false
    statesRef.current.forEach((state) => {
      if (state.finished) return
      const steps = stepsByAlgorithm[state.algorithm]
      applyStep(state, steps[state.pointer])
      anyActive = true
      if (state.pointer >= steps.length) state.finished = true
    })
    forceRender((n) => n + 1)
    if (!anyActive) setPlaying(false)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(stepsByAlgorithm)])

  useEffect(() => {
    if (!playing) return
    intervalRef.current = window.setInterval(stepAll, speedMs)
    return () => {
      if (intervalRef.current) window.clearInterval(intervalRef.current)
    }
  }, [playing, speedMs, stepAll])

  const allFinished = statesRef.current.length > 0 && statesRef.current.every((s) => s.finished)

  return {
    panels: statesRef.current,
    play: () => !allFinished && setPlaying(true),
    pause: () => setPlaying(false),
    step: stepAll,
    reset: init,
    playing,
    allFinished,
  }
}
