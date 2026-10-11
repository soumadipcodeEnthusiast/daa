# REST API Reference

## Endpoints

### 1. `GET /api/algorithms`
Returns complexity and stability metadata for all sorting and searching algorithms.

### 2. `POST /api/trace`
Generates step-by-step array state trace for a single algorithm.
- Body: `{"algorithm": "merge", "array": [4, 2, 7, 1]}`

### 3. `POST /api/race`
Runs multiple algorithms side-by-side on identical generated inputs.
- Body: `{"algorithms": ["bubble", "merge"], "size": 30, "inputType": "random"}`

### 4. `POST /api/benchmark`
Runs repeated executions with JIT warm-up and median timing.
- Body: `{"algorithms": ["quick-random", "heap"], "sizes": [100, 1000], "repetitions": 5}`

### 5. `POST /api/search`
Visualizes linear or binary search step trace.
- Body: `{"algorithm": "binary-search", "array": [1, 3, 5, 7, 9], "target": 5}`
