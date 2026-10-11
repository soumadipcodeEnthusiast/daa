import { type ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import { useGuidedDemo } from '../context/GuidedDemoContext'
import { usePresentation } from '../context/PresentationContext'
import { Button } from './ui'

const NAV_ITEMS = [
  { to: '/',                      label: 'Race Arena' },
  { to: '/benchmark',             label: 'Benchmark Lab' },
  { to: '/lower-bound',           label: 'Lower-Bound Visualizer' },
  { to: '/counting-breaks-bound', label: 'Counting Sort Breaks the Bound' },
  { to: '/quicksort-worst-case',  label: 'Quicksort Worst Case' },
  { to: '/theory',                label: 'Theory Cards' },
  { to: '/search',                label: 'Search Visualizer' },
]

export function Layout({ children }: { children: ReactNode }) {
  const { presentation, togglePresentation } = usePresentation()
  const { active: guidedActive, start: startGuidedDemo } = useGuidedDemo()

  return (
    <div className={`min-h-screen bg-arena-bg bg-grid text-black flex ${presentation ? 'presentation-mode' : ''}`}>
      {!presentation && (
        <aside className="w-64 shrink-0 border-r border-arena-border bg-arena-panel flex flex-col">
          {/* Brand */}
          <div className="px-5 py-5 border-b border-arena-border">
            <div className="text-base font-bold text-black tracking-tight">
              Sorting &amp; Searching
            </div>
            <div className="text-sm font-bold mt-0.5" style={{ color: '#C9A84C' }}>
              Race Arena
            </div>
            <div className="text-xs text-neutral-400 mt-1 font-medium">
              Design &amp; Analysis of Algorithms
            </div>
          </div>

          {/* Nav */}
          <nav className="flex-1 overflow-y-auto py-3">
            {NAV_ITEMS.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                className={({ isActive }) =>
                  `flex items-center px-5 py-2.5 text-sm transition border-l-2 ${
                    isActive
                      ? 'border-[#C9A84C] bg-[#C9A84C]/10 text-black font-semibold'
                      : 'border-transparent text-neutral-500 hover:text-black hover:bg-black/5'
                  }`
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="px-5 py-4 border-t border-arena-border text-xs text-neutral-400">
            Spring Boot &middot; React &middot; TypeScript
          </div>
        </aside>
      )}

      <div className="flex-1 flex flex-col min-w-0">
        {/* Top bar */}
        <header className="flex items-center justify-between gap-4 px-6 py-3 border-b border-arena-border bg-white/80 backdrop-blur-sm">
          <div className="text-sm text-neutral-400 font-medium">
            {presentation ? 'Presentation Mode' : 'Live demo environment'}
          </div>
          <div className="flex items-center gap-2">
            <Button variant="secondary" onClick={startGuidedDemo}>
              Guided Demo
            </Button>
            <Button variant={presentation ? 'primary' : 'secondary'} onClick={togglePresentation}>
              {presentation ? 'Exit Presentation' : 'Presentation Mode'}
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
