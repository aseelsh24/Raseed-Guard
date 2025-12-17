# Decisions

## 1. Mixed Plans Support
- **Decision**: For the initial implementation of the core logic, Mixed Plans (Voice + Internet combined in one package) are not fully supported as a single entity with a unified prediction.
- **Reasoning**: Internet and Voice have different units (MB vs Minutes) and usage patterns.
- **Implication**: They should be treated as two separate tracked items (Plan entries) or unsupported. The current logic normalizes based on the `Unit` field of the `Plan`.

## 2. Rate Calculation Strategy
- **Decision**: Use EWMA (Exponential Weighted Moving Average) for rate calculation.
- **Reasoning**: Simple average is too slow to react to recent changes in behavior. Last interval rate is too volatile. EWMA provides a balance.

## 3. Increasing Balance Handling
- **Decision**: Ignore intervals where balance increases.
- **Reasoning**: An increase implies a top-up or a data correction. Including this as "negative consumption" would skew the rate and prediction logic. We simply skip the interval and restart rate calculation or continue from the next valid drop.

## 4. Minimum Logs
- **Decision**: Require at least 2 logs to calculate a rate.
- **Reasoning**: A rate represents change over time. A single point has no rate.

## 5. UI State Management and Navigation
- **Decision**: The Dashboard ViewModel fetches all plans and their corresponding logs to generate a list of predictions.
- **Reasoning**: The app supports multiple plans simultaneously.
- **Implication**: `DashboardUiState` emits a `List<PlanWithPrediction>`. Navigation arguments are used to pass `planId` for editing.
