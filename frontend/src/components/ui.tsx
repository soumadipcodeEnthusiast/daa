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
      className={`rounded-xl border border-arena-border bg-white shadow-sm p-5 ${className}`}
    >
      {title && (
        <div className="mb-3">
          <h3 className="text-base font-semibold text-neutral-900">{title}</h3>
          {subtitle && <p className="text-sm text-neutral-500 mt-0.5">{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  )
}

export function Skeleton({ className = '' }: { className?: string }) {
  return (
    <div
      className={`animate-pulse rounded-lg bg-neutral-200 ${className}`}
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
    neutral: 'bg-neutral-100 text-neutral-800 border-neutral-200',
    good:    'bg-[#C9A84C]/15 text-[#8C6D23] border-[#C9A84C]/30',
    bad:     'bg-purple-50 text-[#7C3AED] border-purple-200',
    accent:  'bg-neutral-900 text-white border-neutral-900',
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
      'bg-neutral-900 text-white hover:bg-[#C9A84C] hover:text-black border border-neutral-900 hover:border-[#C9A84C] shadow-sm',
    secondary:
      'bg-white text-neutral-800 border border-neutral-300 hover:border-[#C9A84C] hover:text-black shadow-sm',
    ghost:
      'bg-transparent text-neutral-600 hover:bg-neutral-100 hover:text-black',
    danger:
      'bg-purple-50 text-purple-700 border border-purple-200 hover:bg-purple-100',
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
      <span className="pointer-events-none absolute bottom-full left-1/2 mb-2 -translate-x-1/2 whitespace-nowrap rounded-md bg-neutral-900 px-2 py-1 text-xs text-white opacity-0 shadow-lg ring-1 ring-neutral-700 transition group-hover:opacity-100 z-20">
        {label}
      </span>
    </span>
  )
}

export function SectionHeading({ title, description }: { title: string; description?: string }) {
  return (
    <div className="mb-6">
      <h1 className="text-2xl font-bold text-neutral-900 tracking-tight presentation-heading">{title}</h1>
      {description && <p className="mt-1 text-neutral-600 max-w-3xl">{description}</p>}
    </div>
  )
}
