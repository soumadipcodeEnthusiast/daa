/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        arena: {
          bg:      '#ffffff',
          panel:   '#fafafa',
          panel2:  '#f5f5f5',
          border:  '#e5e5e5',
          accent:  '#C9A84C',
          accent2: '#7C3AED',
          compare: '#C9A84C',
          swap:    '#7C3AED',
          pivot:   '#A855F7',
          sorted:  '#171717',
        },
      },
      fontFamily: {
        mono: ['"JetBrains Mono"', 'ui-monospace', 'SFMono-Regular', 'monospace'],
      },
    },
  },
  plugins: [],
}
