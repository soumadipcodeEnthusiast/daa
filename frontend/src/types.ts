export type StepType =
  | 'compare'
  | 'swap'
  | 'overwrite'
  | 'mark-sorted'
  | 'pivot'
  | 'range'
  | 'found'
  | 'not-found'

export interface Step {
  type: StepType
  i: number
  j: number
  value: number
}

export interface SortStats {
  comparisons: number
  swaps: number
  writes: number
  arrayAccesses: number
  elapsedNanos: number
  extraMemoryUnits: number
  stable: boolean
  inPlace: boolean
}

export interface AlgorithmMeta {
  id: string
  name: string
  category: 'comparison' | 'non-comparison'
  bestCase: string
  averageCase: string
  worstCase: string
  spaceComplexity: string
  stable: boolean
  inPlace: boolean
  comparisonBased: boolean
  recurrence: string
  theoremNote: string
}

export interface RaceAlgorithmResult {
  algorithm: string
  steps: Step[]
  stats: SortStats
  finalArray: number[]
}

export interface RaceResponse {
  inputArray: number[]
  results: RaceAlgorithmResult[]
}

export interface TraceResponse {
  steps: Step[]
  stats: SortStats
  finalArray: number[]
}

export interface BenchmarkPoint {
  size: number
  medianNanos: number
  comparisons: number
  extraMemoryUnits: number
  skipped: boolean
  skipReason: string | null
}

export interface BenchmarkSeries {
  algorithm: string
  points: BenchmarkPoint[]
}

export interface BenchmarkRun {
  id: number
  timestamp: string
  algorithm: string
  inputType: string
  size: number
  repetitions: number
  medianNanos: number
  comparisons: number
}

export interface SearchResponse {
  foundIndex: number
  steps: Step[]
  comparisons: number
  elapsedNanos: number
}

export type InputType =
  | 'random'
  | 'sorted'
  | 'reversed'
  | 'nearly-sorted'
  | 'many-duplicates'
  | 'few-unique-large-range'

export const INPUT_TYPES: { value: InputType; label: string }[] = [
  { value: 'random', label: 'Random' },
  { value: 'sorted', label: 'Sorted' },
  { value: 'reversed', label: 'Reversed' },
  { value: 'nearly-sorted', label: 'Nearly sorted' },
  { value: 'many-duplicates', label: 'Many duplicates' },
  { value: 'few-unique-large-range', label: 'Few unique, large range' },
]
