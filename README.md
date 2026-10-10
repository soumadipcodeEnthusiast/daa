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
on `npm run dev`.

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

## Demo script

See the end of this file (added once the Guided Demo is built) for the full
5-minute speaking script that matches the in-app Guided Demo button.
