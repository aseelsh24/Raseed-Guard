# Calculation Rules and Assumptions

## Data Normalization
- **Internet Data**: All values are normalized to Megabytes (MB).
    - 1 GB = 1024 MB.
- **Voice Data**: All values are normalized to Minutes.
- **Mixed Plans**: Currently treated as unsupported or require separate tracking for each type.

## Rate Calculation
- **Method**: The daily consumption rate is calculated based on intervals between consecutive balance logs.
- **Smoothing**: We use Exponential Weighted Moving Average (EWMA) to smooth the consumption rate and avoid drastic changes due to single-day anomalies.
    - Formula: `SmoothedRate = (Alpha * LatestIntervalRate) + ((1 - Alpha) * PreviousSmoothedRate)`
    - **Alpha**: 0.5 (Giving equal weight to the latest interval and the historical average).

## Prediction
- **Depletion Date**: Calculated as `Now + (RemainingAmount / SmoothedDailyRate)`.
- **Safe Daily Usage**: Calculated as `RemainingAmount / DaysUntilPlanExpiry`.

## Risk Levels
The risk level indicates the likelihood of running out of balance before the plan expires.

1. **SAFE**:
    - The predicted depletion date is on or after the plan expiry date.
    - OR we have insufficient data to calculate a rate (Rate <= 0).

2. **WARNING**:
    - The predicted depletion date is *before* the plan expiry date.
    - AND the gap between depletion and expiry is greater than 48 hours.

3. **CRITICAL**:
    - The predicted depletion date is *before* the plan expiry date.
    - AND the gap between depletion and expiry is less than or equal to 48 hours.

## Edge Cases
- **Insufficient Data**: If fewer than 2 logs exist, rate cannot be calculated. Risk is set to SAFE.
- **Top-ups**: If the remaining balance increases between two logs (indicating a top-up or correction), that specific time interval is ignored in the rate calculation.
- **Zero/Negative Time**: Intervals with zero or negative duration are ignored.
