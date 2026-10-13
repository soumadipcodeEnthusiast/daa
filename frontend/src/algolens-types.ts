// Types for AlgoLens API responses

export interface AlgoFinding {
  algorithm: string
  file: string
  method: string
  startLine: number
  endLine: number
  evidence: string
  confidence: number
  timeComplexity: string
  llmExplanation: string | null
  llmConfirmed: boolean
}

export interface AlgoReport {
  id: string
  projectName: string
  overview: string
  findings: AlgoFinding[]
  notes: string[]
  uncertain: string[]
  analyzedAtMs: number
}
