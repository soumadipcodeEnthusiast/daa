import type { ReactNode } from 'react'

export function Card({
  children,
  className = '',
  title,
  subtitle,
}: {
  children: ReactNode
  className?: string
  title?: string
  subtitle?: string
}) {
  return (
    <div
      className={`rounded-2xl border border-arena-border bg-arena-panel/70 backdrop-blur-sm shadow-[0_0_0_1px_rgba(255,255,255,0.02)] p-5 ${className}`}
    >
      {title && (
        <div className="mb-3">
          <h3 className="text-base font-semibold text-slate-100">{title}</h3>
          {subtitle && <p className="text-sm text-slate-400 mt-0.5">{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  )
}

export function Skeleton({ className = '' }: { className?: string }) {
  return (
    <div
      className={`animate-pulse rounded-lg bg-gradient-to-r from-arena-panel2 via-arena-border to-arena-panel2 ${className}`}
    />
  )
}

export function Badge({
  children,
  tone = 'neutral',
}: {
  children: ReactNode
  tone?: 'neutral' | 'good' | 'bad' | 'accent'
}) {
  const tones: Record<string, string> = {
    neutral: 'bg-slate-800 text-slate-300 border-slate-700',
    good: 'bg-emerald-950 text-emerald-300 border-emerald-800',
    bad: 'bg-rose-950 text-rose-300 border-rose-800',
    accent: 'bg-indigo-950 text-indigo-300 border-indigo-800',
  }
  return (
    <span className={`inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-medium ${tones[tone]}`}>
      {children}
    </span>
  )
}

export function Button({
  children,
  onClick,
  variant = 'primary',
  disabled,
  className = '',
  title,
}: {
  children: ReactNode
  onClick?: () => void
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
  disabled?: boolean
  className?: string
  title?: string
}) {
  const variants: Record<string, string> = {
    primary:
      'bg-gradient-to-r from-indigo-600 to-cyan-500 text-white hover:from-indigo-500 hover:to-cyan-400 shadow-lg shadow-indigo-900/40',
    secondary: 'bg-arena-panel2 text-slate-200 border border-arena-border hover:bg-arena-border',
    ghost: 'bg-transparent text-slate-300 hover:bg-arena-panel2',
    danger: 'bg-rose-900/60 text-rose-200 border border-rose-800 hover:bg-rose-900',
  }
  return (
    <button
      title={title}
      disabled={disabled}
      onClick={onClick}
      className={`rounded-lg px-4 py-2 text-sm font-medium transition disabled:opacity-40 disabled:cursor-not-allowed ${variants[variant]} ${className}`}
    >
      {children}
    </button>
  )
}

export function Tooltip({ label, children }: { label: string; children: ReactNode }) {
  return (
    <span className="group relative inline-flex">
      {children}
      <span className="pointer-events-none absolute bottom-full left-1/2 mb-2 -translate-x-1/2 whitespace-nowrap rounded-md bg-slate-900 px-2 py-1 text-xs text-slate-200 opacity-0 shadow-lg ring-1 ring-slate-700 transition group-hover:opacity-100 z-20">
        {label}
      </span>
    </span>
  )
}

export function SectionHeading({ title, description }: { title: string; description?: string }) {
  return (
    <div className="mb-6">
      <h1 className="text-2xl font-bold text-slate-50 tracking-tight presentation-heading">{title}</h1>
      {description && <p className="mt-1 text-slate-400 max-w-3xl">{description}</p>}
    </div>
  )
}
