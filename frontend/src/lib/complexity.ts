export const complexityFns: Record<string, (n: number) => number> = {
  n: (n) => n,
  nlogn: (n) => n * Math.log2(Math.max(n, 2)),
  n2: (n) => n * n,
}

export const complexityLabels: Record<string, string> = {
  n: 'O(n)',
  nlogn: 'O(n log n)',
  n2: 'O(n^2)',
}

/** Scales a reference curve so it passes through (anchorSize, anchorValue), for visual comparison against real data. */
export function scaleCurve(key: keyof typeof complexityFns, anchorSize: number, anchorValue: number) {
  const fn = complexityFns[key]
  const c = anchorValue / Math.max(fn(anchorSize), 1e-9)
  return (n: number) => c * fn(n)
}
