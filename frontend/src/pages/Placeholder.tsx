import { Card, SectionHeading } from '../components/ui'

export function Placeholder({ title }: { title: string }) {
  return (
    <div>
      <SectionHeading title={title} />
      <Card className="text-center text-slate-500 py-16">Coming up in the next build phase.</Card>
    </div>
  )
}
