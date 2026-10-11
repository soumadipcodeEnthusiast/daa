// Categorical palette: White primary bg, Black & Gold secondary, Purple tertiary
export const SERIES_COLORS = [
  '#C9A84C', // Gold
  '#7C3AED', // Purple
  '#171717', // Black
  '#B8973B', // Dark Gold
  '#9333EA', // Vivid Purple
  '#525252', // Charcoal
  '#D4AF37', // Metallic Gold
  '#6D28D9', // Deep Purple
]

export function colorForIndex(i: number) {
  return SERIES_COLORS[i % SERIES_COLORS.length]
}
