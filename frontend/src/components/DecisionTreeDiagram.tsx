import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'

// The minimal decision tree for comparison-sorting 3 elements (a, b, c).
// 6 leaves (one per permutation), height 3 -- matching ceil(log2(6)) = 3,
// illustrating the Omega(n log n) lower bound concretely for n=3.
interface Node {
  id: string
  x: number
  y: number
  label: string
  isLeaf?: boolean
}

const NODES: Node[] = [
  { id: 'root', x: 300, y: 30, label: 'a < b ?' },
  { id: 'L', x: 150, y: 110, label: 'b < c ?' },
  { id: 'R', x: 450, y: 110, label: 'a < c ?' },
  { id: 'LL', x: 75, y: 190, label: 'a < c ?' },
  { id: 'LR', x: 225, y: 190, label: 'a, b, c', isLeaf: true },
  { id: 'RL', x: 375, y: 190, label: 'b, a, c', isLeaf: true },
  { id: 'RR', x: 525, y: 190, label: 'b < c ?' },
  { id: 'LLy', x: 40, y: 270, label: 'a, c, b', isLeaf: true },
  { id: 'LLn', x: 115, y: 270, label: 'c, a, b', isLeaf: true },
  { id: 'RRy', x: 485, y: 270, label: 'b, c, a', isLeaf: true },
  { id: 'RRn', x: 560, y: 270, label: 'c, b, a', isLeaf: true },
]

const EDGES: [string, string][] = [
  ['root', 'L'],
  ['root', 'R'],
  ['L', 'LL'],
  ['L', 'LR'],
  ['R', 'RL'],
  ['R', 'RR'],
  ['LL', 'LLy'],
  ['LL', 'LLn'],
  ['RR', 'RRy'],
  ['RR', 'RRn'],
]

const PATHS: Record<string, string[]> = {
  LR: ['root', 'L', 'LR'],
  RL: ['root', 'R', 'RL'],
  LLy: ['root', 'L', 'LL', 'LLy'],
  LLn: ['root', 'L', 'LL', 'LLn'],
  RRy: ['root', 'R', 'RR', 'RRy'],
  RRn: ['root', 'R', 'RR', 'RRn'],
}

const LEAF_ORDER = ['LR', 'RL', 'LLy', 'LLn', 'RRy', 'RRn']

export function DecisionTreeDiagram() {
  const [activeIdx, setActiveIdx] = useState(0)

  useEffect(() => {
    const id = setInterval(() => setActiveIdx((i) => (i + 1) % LEAF_ORDER.length), 1400)
    return () => clearInterval(id)
  }, [])

  const activeLeaf = LEAF_ORDER[activeIdx]
  const activePath = new Set(PATHS[activeLeaf])
  const byId = Object.fromEntries(NODES.map((n) => [n.id, n]))

  return (
    <svg viewBox="0 0 600 300" className="w-full h-auto">
      {EDGES.map(([from, to]) => {
        const a = byId[from]
        const b = byId[to]
        const onPath = activePath.has(from) && activePath.has(to)
        return (
          <line
            key={`${from}-${to}`}
            x1={a.x}
            y1={a.y + 12}
            x2={b.x}
            y2={b.y - 12}
            stroke={onPath ? '#6366f1' : '#2a3148'}
            strokeWidth={onPath ? 2.5 : 1.5}
          />
        )
      })}
      {NODES.map((n) => {
        const onPath = activePath.has(n.id)
        const isActiveLeaf = n.id === activeLeaf
        return (
          <g key={n.id}>
            <motion.rect
              x={n.x - (n.isLeaf ? 32 : 36)}
              y={n.y - 12}
              width={n.isLeaf ? 64 : 72}
              height={24}
              rx={6}
              fill={isActiveLeaf ? '#4ade80' : n.isLeaf ? '#161c2d' : onPath ? '#1e2538' : '#121725'}
              stroke={onPath ? '#6366f1' : '#232b40'}
              strokeWidth={1.5}
              animate={isActiveLeaf ? { scale: [1, 1.08, 1] } : {}}
              transition={{ duration: 0.6, repeat: isActiveLeaf ? Infinity : 0 }}
            />
            <text
              x={n.x}
              y={n.y + 4}
              textAnchor="middle"
              fontSize={11}
              fontFamily="ui-monospace, monospace"
              fill={isActiveLeaf ? '#052e16' : '#cbd5e1'}
            >
              {n.label}
            </text>
          </g>
        )
      })}
    </svg>
  )
}
