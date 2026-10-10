import { useState } from 'react'
import { Route, Routes } from 'react-router-dom'
import { Layout } from './components/Layout'
import { PresentationProvider } from './context/PresentationContext'
import { ToastProvider } from './context/ToastContext'
import { RaceArena } from './pages/RaceArena'
import { BenchmarkLab } from './pages/BenchmarkLab'
import { Placeholder } from './pages/Placeholder'

function AppRoutes() {
  const [guidedCue, setGuidedCue] = useState(0)

  return (
    <Layout onStartGuidedDemo={() => setGuidedCue((c) => c + 1)}>
      <Routes>
        <Route path="/" element={<RaceArena />} />
        <Route path="/benchmark" element={<BenchmarkLab />} />
        <Route path="/lower-bound" element={<Placeholder title="Lower-Bound Visualizer" />} />
        <Route path="/counting-breaks-bound" element={<Placeholder title="Counting Sort Breaks the Bound" />} />
        <Route path="/quicksort-worst-case" element={<Placeholder title="Quicksort Worst Case" />} />
        <Route path="/theory" element={<Placeholder title="Theory Cards" />} />
        <Route path="/search" element={<Placeholder title="Search Visualizer" />} />
      </Routes>
      {guidedCue > 0 && null}
    </Layout>
  )
}

export default function App() {
  return (
    <ToastProvider>
      <PresentationProvider>
        <AppRoutes />
      </PresentationProvider>
    </ToastProvider>
  )
}
