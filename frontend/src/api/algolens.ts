import type { AlgoReport } from '../algolens-types'

const BASE = '/api/algolens'

class ApiError extends Error {
  status: number
  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

async function checkResponse(res: Response): Promise<Response> {
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
  return res
}

export const algoLensApi = {
  analyzeFile: async (file: File): Promise<AlgoReport> => {
    const form = new FormData()
    form.append('file', file)
    const res = await checkResponse(await fetch(`${BASE}/analyze`, { method: 'POST', body: form }))
    return res.json()
  },

  analyzeGitHub: async (url: string): Promise<AlgoReport> => {
    const form = new FormData()
    form.append('githubUrl', url)
    const res = await checkResponse(await fetch(`${BASE}/analyze`, { method: 'POST', body: form }))
    return res.json()
  },

  markdownUrl: (id: string) => `${BASE}/report/${id}/markdown`,
}

export { ApiError }
