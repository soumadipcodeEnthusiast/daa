import { useState } from 'react'
import { Route, Routes } from 'react-router-dom'
import { Layout } from './components/Layout'
import { PresentationProvider } from './context/PresentationContext'
import { ToastProvider } from './context/ToastContext'
import { RaceArena } from './pages/RaceArena'
import { BenchmarkLab } from './pages/BenchmarkLab'
import { LowerBoundVisualizer } from './pages/LowerBoundVisualizer'
import { CountingSortBreaksBound } from './pages/CountingSortBreaksBound'
import { QuicksortWorstCase } from './pages/QuicksortWorstCase'
import { TheoryCards } from './pages/TheoryCards'
import { SearchVisualizer } from './pages/SearchVisualizer'

function AppRoutes() {
  const [guidedCue, setGuidedCue] = useState(0)

  return (
    <Layout onStartGuidedDemo={() => setGuidedCue((c) => c + 1)}>
      <Routes>
        <Route path="/" element={<RaceArena />} />
        <Route path="/benchmark" element={<BenchmarkLab />} />
        <Route path="/lower-bound" element={<LowerBoundVisualizer />} />
        <Route path="/counting-breaks-bound" element={<CountingSortBreaksBound />} />
        <Route path="/quicksort-worst-case" element={<QuicksortWorstCase />} />
        <Route path="/theory" element={<TheoryCards />} />
        <Route path="/search" element={<SearchVisualizer />} />
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
