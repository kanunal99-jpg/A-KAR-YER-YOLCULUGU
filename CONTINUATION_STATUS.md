# AI Kariyer Yolculuğu — Continuation Status

## Current mission
Turn the project from a demo/mock career application into a real, data-driven AI career platform.

## Non-negotiable rule
Never invent user experience, employers, education, achievements, KPIs, team sizes, salaries, or skills. Any generated result must be traceable to user-provided data or clearly identified as an external/research-derived recommendation.

## Working chain
Detect → Extract → Understand → Research → Match → Evidence → Recommend → Apply → Test → Measure → Update Profile.

## Current architecture
- Jetpack Compose / Material 3 UI
- MVVM
- CareerViewModel as coordinator
- Repository layer
- Room local persistence
- CareerAgentEngine domain/AI logic
- Main flows: Home, Career & CV, Jobs, Interview, Applications

## Highest-priority technical debt
CareerAgentEngine currently contains hardcoded/demo candidate information and must become data-driven. Hardcoded examples must not enter production candidate profiles or scoring.

## Next engineering order
1. Audit Room entities/DAO/converters/models/repository.
2. Audit CareerAgentEngine completely and separate responsibilities where justified.
3. Replace hardcoded CV parsing with structured extraction from actual CV/user input.
4. Make job matching depend only on profile + job data.
5. Make interview generation/evaluation dynamic.
6. Make tailored CV generation use verified profile data only.
7. Verify persistence and state flow across all screens.
8. Clean naming residue: namespace `com.example`, theme `Theme.MyApplication`.
9. Add/strengthen automated tests.
10. Build release APK and verify GitHub Actions artifact.

## Release discipline
Every meaningful feature/fix should be committed, built, tested, and released with an incremented version so the APK can be tested live.

## Current build
GitHub Actions release APK build has previously succeeded on commit `5ff9ec461bf3373305449d9b4a72aa27164cfbfa` (run `33925558698`), producing `app-release` artifact (~15.3 MB). Do not redo keystore troubleshooting unless a new build actually fails.
