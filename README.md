# Sorting & Searching Race Arena

A Design & Analysis of Algorithms showcase: 8 hand-written sort algorithms (plus a
JDK baseline) and 2 search algorithms, each instrumented with comparison/swap/write
counters and a step-by-step trace, served by a Spring Boot API and animated live by
a React frontend.

## Architecture

```
┌─────────────────────────────┐         ┌──────────────────────────────────────┐
│   Browser (localhost:5173   │  HTTP   │     Spring Boot backend :8080         │
│   dev, or :3000 via Docker) │ ──────► │                                        │
│                              │  /api/* │  Controllers                          │
│  React + Vite + TS           │         │   /algorithms /trace /race /benchmark │
│  Tailwind, Recharts,         │ ◄────── │   /search /history                    │
│  Framer Motion                │  JSON  │       │                               │
│                              │         │  AlgorithmRegistry ─ InputGenerator   │
│  Race Arena · Benchmark Lab  │         │       │                               │
│  Lower-Bound · Counting-vs-  │         │  SortAlgorithm impls (bubble,         │
│  bound · Quicksort worst     │         │  insertion, merge, quicksort x2,      │
│  case · Theory Cards ·       │         │  heap, counting, radix, baseline)     │
│  Search Visualizer            │         │  SearchAlgorithm impls (linear,       │
│                              │         │  binary) -- all via TraceRecorder     │
│                              │         │       │                               │
│                              │         │  BenchmarkService ─► H2 (in-memory)   │
└─────────────────────────────┘         └──────────────────────────────────────┘
```

In Docker, the frontend container is nginx serving the built static files and
reverse-proxying `/api/*` to the backend container over the internal compose
network (`frontend/nginx.conf`); in local dev, Vite's dev server does the same
proxying to `localhost:8080` (`frontend/vite.config.ts`).

## Run it

### Option A: Docker Compose (everything, one command)

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080 (H2 console at `/h2-console`, JDBC URL `jdbc:h2:mem:racearena`)

First boot downloads Maven/npm dependencies inside the build stages, which can take
several minutes depending on your network and how busy the machine is -- that's a
one-time cost; the Spring Boot startup banner itself ("Started RaceArenaApplication
in ...") is what tells you the backend is actually ready. If port 8080 or 3000 is
already taken by something else on your machine, change the left-hand side of the
`ports:` mapping in `docker-compose.yml`.

### Option B: Local dev (hot reload, backend in Docker or local)

```bash
# Backend (needs JDK 21 -- `java -version` should say 21; on macOS with
# Homebrew, `export JAVA_HOME=$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home`)
cd backend
mvn spring-boot:run

# Frontend, in another terminal
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The Vite dev server proxies `/api` to
`http://localhost:8080`, so this also works unchanged if the backend is running in
Docker (`docker compose up backend`) instead of locally -- just leave the frontend
on `npm run dev`. If something else on your machine already holds port 8080, run
the backend with `--server.port=<port>` and point the frontend at it with
`BACKEND_PORT=<port> npm run dev`.

### Running the backend tests

```bash
cd backend
mvn test
```

96 JUnit 5 tests: correctness on empty/single/duplicate/negative/sorted/reversed
input across every algorithm, plus a behavioral stability proof (not just the
hardcoded metadata flag) for bubble, insertion, merge, counting and radix sort.

## Project layout

```
backend/   Spring Boot 3 / Java 21 / Maven -- algorithms, REST API, H2, tests
frontend/  React 19 / Vite / TypeScript / Tailwind / Recharts / Framer Motion
docker-compose.yml, backend/Dockerfile, frontend/Dockerfile, frontend/nginx.conf
```

## API

| Endpoint | Method | Purpose |
|---|---|---|
| `/api/algorithms` | GET | Complexity/stability metadata for every algorithm |
| `/api/trace` | POST | Full step trace for one algorithm (array capped at 200) |
| `/api/race` | POST | One shared input, traces for 2-4 algorithms side by side (size capped at 100) |
| `/api/benchmark` | POST | Real timing at large n, JIT warm-up + median of repetitions, saved to H2 |
| `/api/history` | GET | Last 20 benchmark runs |
| `/api/search` | POST | Linear/binary search trace |

## Presenting it

- **Presentation Mode** (top-right button) drops the sidebar, enlarges headings,
  and hides the dense multi-input configuration panels on Race Arena and
  Benchmark Lab, leaving just the chart/animation and the action buttons visible.
- **Guided Demo** (top-right button) drives a scripted, presenter-paced 5-step
  walkthrough: it navigates to the right page, auto-configures and auto-runs
  each one, and shows a caption bar at the bottom with Prev/Next/Exit. You
  advance it manually while you talk, so it never gets ahead of (or behind) you.

## 5-minute speaking script (matches the Guided Demo)

**Step 1 -- Race Arena (`/`): the race of O(n²) vs O(n log n)** *(~60s)*
> "Here's bubble sort and merge sort, racing on the exact same random array of 40
> numbers. Watch the comparisons counter on the left climb -- bubble sort is doing
> roughly n² work, about 800 comparisons for 40 elements. Merge sort does maybe 150,
> because it's n log n. They finish at completely different speeds even though
> they're solving the identical problem."

**Step 2 -- Lower-Bound Visualizer (`/lower-bound`): why O(n log n) is a floor** *(~70s)*
> "This isn't a coincidence of these particular algorithms -- it's a hard limit.
> Model any comparison-based sort as a decision tree: every comparison is a branch,
> every leaf is one possible output order. Sorting n elements needs n! possible
> leaves, so the tree's height -- which is the worst-case number of comparisons --
> is at least log2(n!), which by Stirling's approximation is Theta(n log n). You can
> see merge sort, heap sort, and randomized quicksort all measuring right at that
> line, for n from 4 up to 2,000. And here's the actual decision tree for sorting
> just 3 elements: 6 leaves, one per permutation, minimum height 3 -- exactly
> ceil(log2(6))."

**Step 3 -- Counting Sort Breaks the Bound (`/counting-breaks-bound`)** *(~60s)*
> "So how does counting sort claim to be faster than the bound allows? It cheats --
> in a good way. It never compares two elements against each other; it counts
> occurrences per key and places them by prefix sum. No comparisons means the
> Omega(n log n) argument doesn't apply at all. Here it is next to merge sort out to
> a million elements, looking essentially linear. But look at the slider: that
> speed is conditional on the key range k staying small. As I drag k up past a few
> hundred thousand even at a modest n, the O(n+k) term takes over and both the time
> and the memory blow up -- that's the tradeoff you're actually paying for."

**Step 4 -- Quicksort Worst Case (`/quicksort-worst-case`)** *(~60s)*
> "Last one: quicksort's worst case isn't really about quicksort, it's about pivot
> selection. Both of these are the same algorithm, run on the same already-sorted
> input. The first-element-pivot version always picks the smallest remaining
> element as pivot, so every partition splits into sizes 0 and n-1 -- that's the
> classic adversarial O(n²) case, and you can see its comparison count land almost
> exactly on n(n-1)/2. Randomize the pivot choice -- one line of code -- and the
> same input now costs about 1.39·n·log2(n) comparisons in expectation, which is
> what you see the random-pivot line tracking."

**Step 5 -- Summary** *(~30s)*
> "Three takeaways: comparison sorts have a real, provable floor at Omega(n log n).
> Non-comparison sorts can beat that floor, but only by trading it for a dependence
> on the key range. And a worst case is often a property of a design decision --
> like which element you pick as pivot -- not an inherent property of the
> algorithm itself. Everything on these slides was measured live against the
> theory, not asserted."
