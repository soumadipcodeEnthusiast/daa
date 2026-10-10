// Validated categorical palette (dark-mode steps), fixed order -- see dataviz skill.
export const SERIES_COLORS = [
  '#3987e5', // blue
  '#d95926', // orange
  '#199e70', // aqua
  '#c98500', // yellow
  '#d55181', // magenta
  '#9085e9', // violet
  '#e66767', // red
  '#4ade80', // green (sorted accent, used last)
]

export function colorForIndex(i: number) {
  return SERIES_COLORS[i % SERIES_COLORS.length]
}
