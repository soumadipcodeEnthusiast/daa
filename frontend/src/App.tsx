import { ToastProvider } from './context/ToastContext'
import { AlgoLens } from './pages/AlgoLens'

export default function App() {
  return (
    <ToastProvider>
      <div className="min-h-screen bg-arena-bg bg-grid text-black">
        <header className="px-6 py-4 border-b border-arena-border bg-white/80 backdrop-blur-sm flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-neutral-900 flex items-center justify-center font-bold text-sm text-[#C9A84C] border border-neutral-800 shadow-sm">
              CR
            </div>
            <div>
              <div className="text-base font-bold text-black tracking-tight leading-none">Code Reverse</div>
              <div className="text-xs font-semibold mt-0.5" style={{ color: '#C9A84C' }}>Algorithm &amp; AST Analysis</div>
            </div>
          </div>
          <div className="text-xs text-neutral-500 font-mono hidden sm:block">
            Reverse Engineering &amp; Complexity Estimation
          </div>
        </header>

        <main className="p-6 max-w-7xl mx-auto">
          <AlgoLens />
        </main>
      </div>
    </ToastProvider>
  )
}
