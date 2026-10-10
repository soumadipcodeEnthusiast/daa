import { createContext, useContext, useState, type ReactNode } from 'react'

interface PresentationContextValue {
  presentation: boolean
  togglePresentation: () => void
}

const PresentationContext = createContext<PresentationContextValue | null>(null)

export function PresentationProvider({ children }: { children: ReactNode }) {
  const [presentation, setPresentation] = useState(false)
  return (
    <PresentationContext.Provider
      value={{ presentation, togglePresentation: () => setPresentation((p) => !p) }}
    >
      {children}
    </PresentationContext.Provider>
  )
}

export function usePresentation() {
  const ctx = useContext(PresentationContext)
  if (!ctx) throw new Error('usePresentation must be used within PresentationProvider')
  return ctx
}
