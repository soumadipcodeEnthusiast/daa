import { useCallback, useEffect, useRef, useState } from 'react'
import type { RaceAlgorithmResult, Step } from '../types'

export type HighlightKind = 'compare' | 'swap' | 'pivot'

export interface PanelState {
  algorithm: string
  array: number[]
  highlight: Map<number, HighlightKind>
  sorted: Set<number>
  comparisons: number
  swapsWrites: number
  pointer: number
  totalSteps: number
  finished: boolean
  finishRank: number | null
}

function applyStep(state: PanelState, step: Step) {
  state.highlight = new Map()
  switch (step.type) {
    case 'compare':
      state.comparisons++
      state.highlight.set(step.i, 'compare')
      if (step.j >= 0) state.highlight.set(step.j, 'compare')
      break
    case 'swap': {
      state.swapsWrites++
      const tmp = state.array[step.i]
      state.array[step.i] = state.array[step.j]
      state.array[step.j] = tmp
      state.highlight.set(step.i, 'swap')
      state.highlight.set(step.j, 'swap')
      break
    }
    case 'overwrite':
      state.swapsWrites++
      state.array[step.i] = step.value
      state.highlight.set(step.i, 'swap')
      break
    case 'mark-sorted':
      state.sorted.add(step.i)
      break
    case 'pivot':
      state.highlight.set(step.i, 'pivot')
      break
  }
  state.pointer++
}

export function useRaceEngine(results: RaceAlgorithmResult[], inputArray: number[], speedMs: number) {
  const [playing, setPlaying] = useState(false)
  const [, forceRender] = useState(0)
  const statesRef = useRef<PanelState[]>([])
  const finishOrderRef = useRef<string[]>([])
  const intervalRef = useRef<number | null>(null)

  const init = useCallback(() => {
    statesRef.current = results.map((r) => ({
      algorithm: r.algorithm,
      array: inputArray.slice(),
      highlight: new Map(),
      sorted: new Set(),
      comparisons: 0,
      swapsWrites: 0,
      pointer: 0,
      totalSteps: r.steps.length,
      finished: r.steps.length === 0,
      finishRank: null,
    }))
    finishOrderRef.current = []
    setPlaying(false)
    forceRender((n) => n + 1)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [results, inputArray])

  useEffect(() => {
    init()
  }, [init])

  const stepAll = useCallback(() => {
    let anyActive = false
    statesRef.current.forEach((state, idx) => {
      if (state.finished) return
      const steps = results[idx].steps
      applyStep(state, steps[state.pointer])
      anyActive = true
      if (state.pointer >= steps.length) {
        state.finished = true
        finishOrderRef.current.push(state.algorithm)
        state.finishRank = finishOrderRef.current.length
      }
    })
    forceRender((n) => n + 1)
    if (!anyActive) setPlaying(false)
  }, [results])

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
