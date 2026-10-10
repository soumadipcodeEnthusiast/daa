/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        arena: {
          bg: '#0b0e14',
          panel: '#121725',
          panel2: '#161c2d',
          border: '#232b40',
          accent: '#6366f1',
          accent2: '#22d3ee',
          compare: '#facc15',
          swap: '#f87171',
          pivot: '#c084fc',
          sorted: '#4ade80',
        },
      },
      fontFamily: {
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
    },
  },
  plugins: [],
}
