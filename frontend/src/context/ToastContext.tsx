import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'

type ToastVariant = 'info' | 'error' | 'success'

interface Toast {
  id: number
  message: string
  variant: ToastVariant
}

interface ToastContextValue {
  push: (message: string, variant?: ToastVariant) => void
}

const ToastContext = createContext<ToastContextValue | null>(null)

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([])

  const push = useCallback((message: string, variant: ToastVariant = 'info') => {
    const id = Date.now() + Math.random()
    setToasts((prev) => [...prev, { id, message, variant }])
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, 3500)
  }, [])

  return (
    <ToastContext.Provider value={{ push }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2 max-w-sm pointer-events-none">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`rounded-lg border px-4 py-3 text-sm shadow-md backdrop-blur-sm animate-[fadeIn_0.2s_ease-out] font-medium ${
              t.variant === 'error'
                ? 'border-red-200 bg-red-50 text-red-900'
                : t.variant === 'success'
                  ? 'border-[#C9A84C]/40 bg-[#fefbf3] text-[#8C6D23]'
                  : 'border-purple-200 bg-purple-50 text-purple-900'
            }`}
          >
            {t.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used within ToastProvider')
  return ctx
}
