const ITEMS = [
  { label: 'Comparing', color: 'bg-arena-compare' },
  { label: 'Swapping', color: 'bg-arena-swap' },
  { label: 'Pivot', color: 'bg-arena-pivot' },
  { label: 'Sorted', color: 'bg-arena-sorted' },
  { label: 'Idle', color: 'bg-slate-600' },
]

export function Legend() {
  return (
    <div className="flex flex-wrap items-center gap-4 text-xs text-slate-400">
      {ITEMS.map((item) => (
        <div key={item.label} className="flex items-center gap-1.5">
          <span className={`h-2.5 w-2.5 rounded-sm ${item.color}`} />
          {item.label}
        </div>
      ))}
    </div>
  )
}
