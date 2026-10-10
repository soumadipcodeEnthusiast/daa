import { type ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import { useGuidedDemo } from '../context/GuidedDemoContext'
import { usePresentation } from '../context/PresentationContext'
import { Button } from './ui'

const NAV_ITEMS = [
  { to: '/', label: 'Race Arena', icon: '🏁' },
  { to: '/benchmark', label: 'Benchmark Lab', icon: '📊' },
  { to: '/lower-bound', label: 'Lower-Bound Visualizer', icon: '📐' },
  { to: '/counting-breaks-bound', label: 'Counting Sort Breaks the Bound', icon: '🧮' },
  { to: '/quicksort-worst-case', label: 'Quicksort Worst Case', icon: '⚡' },
  { to: '/theory', label: 'Theory Cards', icon: '📚' },
  { to: '/search', label: 'Search Visualizer', icon: '🔍' },
]

export function Layout({ children }: { children: ReactNode }) {
  const { presentation, togglePresentation } = usePresentation()
  const { active: guidedActive, start: startGuidedDemo } = useGuidedDemo()

  return (
    <div className={`min-h-screen bg-arena-bg bg-grid text-slate-200 flex ${presentation ? 'presentation-mode' : ''}`}>
      {!presentation && (
        <aside className="w-64 shrink-0 border-r border-arena-border bg-arena-panel/60 backdrop-blur-sm flex flex-col">
          <div className="px-5 py-5 border-b border-arena-border">
            <div className="text-lg font-bold text-white tracking-tight">
              Sorting &amp; Searching
            </div>
            <div className="text-sm text-indigo-300 font-semibold">Race Arena</div>
            <div className="text-xs text-slate-500 mt-1">Design &amp; Analysis of Algorithms</div>
          </div>
          <nav className="flex-1 overflow-y-auto py-3">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-5 py-2.5 text-sm transition border-l-2 ${
                    isActive
                      ? 'border-indigo-400 bg-indigo-500/10 text-white font-medium'
                      : 'border-transparent text-slate-400 hover:text-slate-200 hover:bg-white/5'
                  }`
                }
              >
                <span>{item.icon}</span>
                {item.label}
              </NavLink>
            ))}
          </nav>
          <div className="px-5 py-4 border-t border-arena-border text-xs text-slate-500">
            Backend: Spring Boot · Frontend: React
          </div>
        </aside>
      )}

      <div className="flex-1 flex flex-col min-w-0">
        <header className="flex items-center justify-between gap-4 px-6 py-3 border-b border-arena-border bg-arena-panel/40 backdrop-blur-sm">
          <div className="text-sm text-slate-400">
            {presentation ? 'Presentation Mode' : 'Live demo environment'}
          </div>
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={startGuidedDemo}>
              ▶ Guided Demo
            </Button>
            <Button variant={presentation ? 'primary' : 'secondary'} onClick={togglePresentation}>
              {presentation ? '⤢ Exit Presentation' : '⛶ Presentation Mode'}
            </Button>
          </div>
        </header>
        <main className={`flex-1 overflow-y-auto p-6 ${presentation ? 'text-lg presentation-scale' : ''} ${guidedActive ? 'pb-24' : ''}`}>
          {children}
        </main>
      </div>
    </div>
  )
}
