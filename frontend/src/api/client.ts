import type {
  AlgorithmMeta,
  BenchmarkRun,
  BenchmarkSeries,
  InputType,
  RaceResponse,
  SearchResponse,
  TraceResponse,
} from '../types'

const BASE = '/api'

class ApiError extends Error {
  status: number
  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    let message = `Request failed with status ${res.status}`
    try {
      const body = await res.json()
      if (body?.message) message = body.message
    } catch {
      // ignore
    }
    throw new ApiError(res.status, message)
  }
  return res.json() as Promise<T>
}

export const api = {
  getAlgorithms: () => request<AlgorithmMeta[]>('/algorithms'),

  trace: (algorithm: string, array: number[]) =>
    request<TraceResponse>('/trace', {
      method: 'POST',
      body: JSON.stringify({ algorithm, array }),
    }),

  race: (algorithms: string[], inputType: InputType, size: number, seed: number) =>
    request<RaceResponse>('/race', {
      method: 'POST',
      body: JSON.stringify({ algorithms, inputType, size, seed }),
    }),

  benchmark: (algorithms: string[], inputType: InputType, sizes: number[], repetitions: number) =>
    request<BenchmarkSeries[]>('/benchmark', {
      method: 'POST',
      body: JSON.stringify({ algorithms, inputType, sizes, repetitions }),
    }),

  history: () => request<BenchmarkRun[]>('/history'),

  search: (algorithm: string, array: number[], target: number) =>
    request<SearchResponse>('/search', {
      method: 'POST',
      body: JSON.stringify({ algorithm, array, target }),
    }),
}

export { ApiError }
