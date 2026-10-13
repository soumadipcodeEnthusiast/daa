import { useRef, useState } from 'react'
import { algoLensApi } from '../api/algolens'
import { Badge, Button, Card, SectionHeading, Skeleton } from '../components/ui'
import type { AlgoFinding, AlgoReport } from '../algolens-types'
import { DEMO_JAVA_CODE } from '../demoAlgorithmsCode'

type InputMode = 'file' | 'github'

export function AlgoLens() {
  const [mode, setMode] = useState<InputMode>('file')
  const [githubUrl, setGithubUrl] = useState('')
  const [file, setFile] = useState<File | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [report, setReport] = useState<AlgoReport | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  function loadDemoFile() {
    setMode('file')
    const demo = new File([DEMO_JAVA_CODE], 'DemoAlgorithms.java', { type: 'text/plain' })
    setFile(demo)
    setError(null)
  }

  async function run() {
    setError(null)
    setLoading(true)
    try {
      let result: AlgoReport
      if (mode === 'file') {
        if (!file) { setError('Select a .java or .zip file first.'); setLoading(false); return }
        result = await algoLensApi.analyzeFile(file)
      } else {
        if (!githubUrl.trim()) { setError('Enter a GitHub URL first.'); setLoading(false); return }
        result = await algoLensApi.analyzeGitHub(githubUrl.trim())
      }
      setReport(result)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Analysis failed.')
    } finally {
      setLoading(false)
    }
  }

  function confidenceTone(c: number): 'good' | 'neutral' | 'bad' {
    if (c >= 0.75) return 'good'
    if (c >= 0.5)  return 'neutral'
    return 'bad'
  }

  return (
    <div>
      <SectionHeading
        title="Code Reverse"
        description="Paste a Java file, upload a zip, or give a public GitHub URL. The tool reverse-engineers code to identify algorithms, data structures, and estimated complexity."
      />

      {/* Input Card */}
      <Card className="mb-6">
        {/* Mode toggle */}
        <div className="flex gap-2 mb-5">
          {(['file', 'github'] as InputMode[]).map((m) => (
            <button
              key={m}
              onClick={() => setMode(m)}
              className={`rounded-lg border px-3 py-1.5 text-sm font-medium transition ${
                mode === m
                  ? 'border-[#C9A84C] bg-[#C9A84C]/15 text-[#8C6D23]'
                  : 'border-arena-border bg-white text-neutral-700 hover:text-black hover:border-neutral-400'
              }`}
            >
              {m === 'file' ? 'File upload (.java / .zip)' : 'GitHub URL'}
            </button>
          ))}
        </div>

        {mode === 'file' ? (
          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
              Java file or zip archive
            </label>
            <div
              className="mt-2 flex items-center gap-3 rounded-lg border border-dashed border-arena-border bg-arena-panel px-4 py-5 cursor-pointer hover:border-[#C9A84C] transition"
              onClick={() => fileInputRef.current?.click()}
            >
              <span className="text-sm text-neutral-600">
                {file ? file.name : 'Click to choose a file — or drag and drop'}
              </span>
              {file && (
                <span className="ml-auto text-xs text-neutral-400">
                  {(file.size / 1024).toFixed(0)} KB
                </span>
              )}
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept=".java,.zip"
              className="hidden"
              onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            />
          </div>
        ) : (
          <div>
            <label className="text-xs uppercase tracking-wide text-neutral-500 font-semibold">
              GitHub repository URL (https only)
            </label>
            <input
              type="url"
              value={githubUrl}
              onChange={(e) => setGithubUrl(e.target.value)}
              placeholder="https://github.com/owner/repo"
              className="mt-2 w-full rounded-lg border border-arena-border bg-white px-3 py-2 text-sm font-mono text-neutral-900 shadow-sm focus:border-[#C9A84C] focus:outline-none"
            />
          </div>
        )}

        <div className="mt-5 flex items-center gap-3 flex-wrap">
          <Button onClick={run} disabled={loading}>
            {loading ? 'Analysing…' : 'Analyse'}
          </Button>
          <Button
            variant="secondary"
            onClick={loadDemoFile}
            disabled={loading}
            title="Load DemoAlgorithms.java (QuickSort, Binary Search, Two Pointers, Bubble Sort, BFS, DFS, DP, Heap)"
          >
            Load Demo Sample
          </Button>
          {report && (
            <Button
              variant="secondary"
              onClick={() => window.open(algoLensApi.markdownUrl(report.id), '_blank')}
            >
              Download Markdown
            </Button>
          )}
          {error && (
            <p className="text-sm text-[#8C6D23] font-medium">{error}</p>
          )}
        </div>
      </Card>

      {/* Skeleton while loading */}
      {loading && (
        <div className="space-y-4">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-24" />
          ))}
        </div>
      )}

      {/* Empty state */}
      {!report && !loading && (
        <Card className="text-center text-neutral-500 py-16">
          Choose a file or URL and click <span className="text-neutral-900 font-semibold">Analyse</span>.
        </Card>
      )}

      {/* Report */}
      {report && !loading && (
        <div className="space-y-5">
          {/* Overview */}
          <Card title={report.projectName} subtitle="Overview">
            <p className="text-sm text-neutral-700 leading-relaxed">{report.overview}</p>
          </Card>

          {/* Summary table */}
          {report.findings.length > 0 && (
            <Card title="Algorithms Found">
              <div className="overflow-x-auto">
                <table className="min-w-full text-sm">
                  <thead>
                    <tr className="border-b border-arena-border text-left text-xs uppercase tracking-wide text-neutral-500">
                      <th className="pb-2 pr-4 font-semibold">Algorithm</th>
                      <th className="pb-2 pr-4 font-semibold">Where</th>
                      <th className="pb-2 pr-4 font-semibold">Complexity</th>
                      <th className="pb-2 font-semibold">Confidence</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.findings.map((f, i) => (
                      <tr key={i} className="border-b border-arena-border/50 last:border-0">
                        <td className="py-2 pr-4 font-medium text-neutral-900">{f.algorithm}</td>
                        <td className="py-2 pr-4 font-mono text-xs text-neutral-600 max-w-xs truncate">
                          {f.file}{f.method ? ' / ' + f.method : ''}
                        </td>
                        <td className="py-2 pr-4 text-neutral-700">{f.timeComplexity}</td>
                        <td className="py-2">
                          <Badge tone={confidenceTone(f.confidence)}>
                            {Math.round(f.confidence * 100)}%
                          </Badge>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>
          )}

          {/* Detail cards */}
          {report.findings.map((f, i) => (
            <FindingCard key={i} finding={f} />
          ))}

          {/* Notes */}
          {report.notes.length > 0 && (
            <Card title="Things Worth Noting">
              <ul className="space-y-2">
                {report.notes.map((n, i) => (
                  <li key={i} className="text-sm text-neutral-700 leading-relaxed">
                    <span className="mr-2 text-neutral-400">—</span>{n}
                  </li>
                ))}
              </ul>
            </Card>
          )}

          {/* Uncertain */}
          {report.uncertain.length > 0 && (
            <Card title="Uncertain">
              <ul className="space-y-1">
                {report.uncertain.map((u, i) => (
                  <li key={i} className="text-sm text-neutral-500 font-mono">{u}</li>
                ))}
              </ul>
            </Card>
          )}
        </div>
      )}
    </div>
  )
}

function FindingCard({ finding: f }: { finding: AlgoFinding }) {
  return (
    <Card>
      <div className="flex items-start justify-between gap-4 mb-3">
        <div>
          <h3 className="text-base font-semibold text-neutral-900">{f.algorithm}</h3>
          <p className="text-xs font-mono text-neutral-500 mt-0.5">
            {f.file}{f.method ? ' / ' + f.method : ''}
            {f.startLine > 0 ? ` · lines ${f.startLine}–${f.endLine}` : ''}
          </p>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          <span className="text-xs text-neutral-500">{f.timeComplexity}</span>
          {!f.llmConfirmed && (
            <Badge tone="bad">LLM rejected</Badge>
          )}
        </div>
      </div>

      <div className="space-y-2 text-sm">
        <div>
          <span className="text-xs font-semibold uppercase tracking-wide text-neutral-400">Evidence</span>
          <p className="mt-0.5 text-neutral-700">{f.evidence}</p>
        </div>

        {f.llmExplanation && (
          <div>
            <span className="text-xs font-semibold uppercase tracking-wide text-neutral-400">
              {f.llmConfirmed ? 'LLM confirms' : 'LLM disagrees'}
            </span>
            <p className="mt-0.5 text-neutral-700">{f.llmExplanation}</p>
          </div>
        )}
      </div>
    </Card>
  )
}
