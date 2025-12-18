# Decisions

## Domain Model
- `Plan` and `BalanceLog` are pure Kotlin data classes.
- `UsagePredictor` contains pure business logic.

## Persistence
- Room is used for local storage.
- KSP is used for annotation processing.
- `AppDatabase` is the single source of truth.
- `PlanRepository` and `BalanceLogRepository` abstract data access.

## UI Integration (Room + KSP)
- **Room+KSP + storage integration**:
    - Wired `DashboardViewModel`, `AddEditPlanViewModel`, and `WeeklyUpdateViewModel` to real Room Repositories.
    - Used `AppViewModelProvider` (ViewModel Factory) for dependency injection of repositories into ViewModels.
    - Replaced mock data in `DashboardViewModel` with `combine` flow of plans and logs.
    - `AddEditPlanViewModel` handles Plan creation.
    - `WeeklyUpdateViewModel` handles Balance Log insertion.

## Architecture
- **MVVM**: ViewModels hold state (`StateFlow`) and expose it to Composables.
- **Unidirectional Data Flow**: UI observes state, User actions trigger ViewModel functions, ViewModel updates Repository/State.
- **Dependency Injection**: Manual DI via `AppContainer` passed to `AppViewModelProvider`.
