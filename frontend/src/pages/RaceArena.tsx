import { useEffect, useMemo, useState } from 'react'
import { api } from '../api/client'
import { Badge, Button, Card, SectionHeading, Skeleton } from '../components/ui'
import { Legend } from '../components/Legend'
import { RacePanel } from '../components/RacePanel'
import { useRaceEngine } from '../hooks/useRaceEngine'
import { useGuidedDemo } from '../context/GuidedDemoContext'
import { useToast } from '../context/ToastContext'
import { INPUT_TYPES, type AlgorithmMeta, type InputType, type RaceResponse } from '../types'

const DEFAULT_SELECTION = ['bubble', 'merge', 'quick-random', 'quick-first']

interface RaceOverrides {
  algorithms: string[]
  inputType: InputType
  size: number
  seed: number
}

export function RaceArena() {
  const { push } = useToast()
  const [algorithms, setAlgorithms] = useState<AlgorithmMeta[]>([])
  const [selected, setSelected] = useState<string[]>(DEFAULT_SELECTION)
  const [inputType, setInputType] = useState<InputType>('random')
  const [size, setSize] = useState(30)
  const [seed, setSeed] = useState(42)
  const [speedMs, setSpeedMs] = useState(80)
  const [loading, setLoading] = useState(false)
  const [race, setRace] = useState<RaceResponse | null>(null)

  const { active: guidedActive, stepIndex: guidedStepIndex, steps: guidedSteps } = useGuidedDemo()
  const raceStepIndex = guidedSteps.findIndex((s) => s.route === '/')

  useEffect(() => {
    api
      .getAlgorithms()
      .then((all) => setAlgorithms(all.filter((a) => !a.id.endsWith('-search'))))
      .catch(() => push('Could not load algorithm list from backend', 'error'))
  }, [push])

  const engine = useRaceEngine(race?.results ?? [], race?.inputArray ?? [], speedMs)

  const maxValue = useMemo(() => {
    if (!race) return 1
    return Math.max(1, ...race.inputArray)
  }, [race])

  async function startRace(overrides?: RaceOverrides) {
    const algos = overrides?.algorithms ?? selected
    const type = overrides?.inputType ?? inputType
    const sz = overrides?.size ?? size
    const sd = overrides?.seed ?? seed
    if (algos.length < 2 || algos.length > 4) {
      push('Pick between 2 and 4 algorithms to race', 'error')
      return
    }
    setLoading(true)
    try {
      const result = await api.race(algos, type, sz, sd)
      setRace(result)
    } catch (e) {
      push(e instanceof Error ? e.message : 'Race failed', 'error')
    } finally {
      setLoading(false)
    }
  }

  // Guided Demo step 1: auto-configure a slow O(n^2) sort against a fast O(n log n) one, and start the race.
  useEffect(() => {
    if (guidedActive && guidedStepIndex === raceStepIndex) {
      const demoAlgorithms = ['bubble', 'merge']
      setSelected(demoAlgorithms)
      setInputType('random')
      setSize(40)
      setSeed(42)
      startRace({ algorithms: demoAlgorithms, inputType: 'random', size: 40, seed: 42 })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [guidedActive, guidedStepIndex])

  // Once the guided demo's race data lands, auto-play it.
  useEffect(() => {
    if (guidedActive && guidedStepIndex === raceStepIndex && race) {
      engine.play()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [race])

  function toggleAlgo(id: string) {
    setSelected((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]))
  }

  const sortAlgos = algorithms.filter((a) => a.id !== 'linear-search' && a.id !== 'binary-search')

  return (
    <div>
      <SectionHeading
        title="Race Arena"
        description="Pick 2-4 algorithms, generate one shared input, and watch them sort it side by side."
      />

      <Card className="mb-6">
        <div data-controls="true" className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          <div className="lg:col-span-2">
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
              Algorithms ({selected.length}/4)
            </label>
            <div className="mt-2 flex flex-wrap gap-2">
              {sortAlgos.length === 0 &&
                Array.from({ length: 6 }).map((_, i) => <Skeleton key={i} className="h-8 w-28" />)}
              {sortAlgos.map((a) => {
                const active = selected.includes(a.id)
                return (
                  <button
                    key={a.id}
                    onClick={() => toggleAlgo(a.id)}
                    className={`rounded-lg border px-3 py-1.5 text-sm transition font-medium ${
                      active
                        ? 'border-[#C9A84C] bg-[#C9A84C]/15 text-[#8C6D23]'
                        : 'border-arena-border bg-white text-neutral-700 hover:text-black hover:border-neutral-400'
                    }`}
                  >
                    {a.name}
                  </button>
                )
              })}
            </div>
          </div>

          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
              Input type
            </label>
            <select
              value={inputType}
              onChange={(e) => setInputType(e.target.value as InputType)}
              className="mt-2 w-full rounded-lg border border-arena-border bg-white px-3 py-2 text-sm text-neutral-900 shadow-sm focus:border-[#C9A84C] focus:outline-none"
            >
              {INPUT_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold mt-4 block">
              Seed
            </label>
            <input
              type="number"
              value={seed}
              onChange={(e) => setSeed(Number(e.target.value))}
              className="mt-2 w-full rounded-lg border border-arena-border bg-white px-3 py-2 text-sm text-neutral-900 shadow-sm focus:border-[#C9A84C] focus:outline-none"
            />
          </div>

          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
              Size: <span className="text-neutral-900 font-mono font-bold">{size}</span>
            </label>
            <input
              type="range"
              min={10}
              max={100}
              value={size}
              onChange={(e) => setSize(Number(e.target.value))}
              className="mt-3 w-full"
            />
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold mt-4 block">
              Speed: <span className="text-neutral-900 font-mono font-bold">{speedMs}ms/step</span>
            </label>
            <input
              type="range"
              min={10}
              max={400}
              value={speedMs}
              onChange={(e) => setSpeedMs(Number(e.target.value))}
              className="mt-3 w-full"
            />
          </div>
        </div>

        <div className="mt-5 flex items-center gap-3">
          <Button onClick={() => startRace()} disabled={loading}>
            {loading ? 'Generating…' : 'Start Race'}
          </Button>
          {race && (
            <>
              <Button variant="secondary" onClick={engine.playing ? engine.pause : engine.play} disabled={engine.allFinished}>
                {engine.playing ? 'Pause' : 'Play'}
              </Button>
              <Button variant="secondary" onClick={engine.step} disabled={engine.playing || engine.allFinished}>
                Step
              </Button>
              <Button variant="secondary" onClick={engine.reset}>
                Reset
              </Button>
            </>
          )}
          <div className="flex-1" />
          <Legend />
        </div>
      </Card>

      {!race && !loading && (
        <Card className="text-center text-neutral-500 py-16">
          Configure the race above and click <span className="text-neutral-900 font-semibold">Start Race</span>.
        </Card>
      )}

      {loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {Array.from({ length: selected.length || 2 }).map((_, i) => (
            <Skeleton key={i} className="h-64" />
          ))}
        </div>
      )}

      {race && !loading && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {engine.panels.map((panel) => (
            <RacePanel
              key={panel.algorithm}
              state={panel}
              displayName={algorithms.find((a) => a.id === panel.algorithm)?.name ?? panel.algorithm}
              maxValue={maxValue}
            />
          ))}
        </div>
      )}

      {race && engine.allFinished && (
        <Card className="mt-4" title="Finish order">
          <div className="flex flex-wrap gap-3">
            {[...engine.panels]
              .sort((a, b) => (a.finishRank ?? 999) - (b.finishRank ?? 999))
              .map((p) => (
                <Badge key={p.algorithm} tone={p.finishRank === 1 ? 'good' : 'neutral'}>
                  #{p.finishRank} {algorithms.find((a) => a.id === p.algorithm)?.name ?? p.algorithm} —{' '}
                  {p.comparisons} comparisons, {p.swapsWrites} swaps/writes
                </Badge>
              ))}
          </div>
        </Card>
      )}
    </div>
  )
}
