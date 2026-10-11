# Architecture & Trace Pipeline

## System Layers

1. **Frontend (SPA)**: React 19 + TypeScript + Vite + Tailwind CSS + Recharts
2. **REST API**: Spring Boot 3 running on Java 21
3. **Trace Engine**: `TraceRecorder` intercepts comparison, swap, and write events
4. **Data Persistence**: H2 in-memory relational database storing benchmark history

## Data Flow Pipeline

```
User Action -> Frontend API Client -> Spring Boot Controller -> Algorithm Execution
                                                                        |
                                                                        v
Screen Animation <- State Array Replay <- JSON Step Traces <- TraceRecorder
```
