# AI Development Roadmap

## Phase 1 — Data truth
- Remove hardcoded candidate facts from production paths.
- Define verified candidate profile fields and provenance.
- Preserve unknown fields as unknown; ask the user instead of guessing.

## Phase 2 — Career intelligence
- CV extraction → structured profile.
- Career discovery from verified experience.
- Explainable job matching with component scores.
- Gap analysis and development planning.

## Phase 3 — Adaptive AI
- Position-specific interview generation.
- Answer evaluation grounded in the target role.
- Tailored CV generation using verified facts only.
- Conversational career coach grounded in profile/application context.

## Phase 4 — Production data
- Real job-source integration where legally/technically available.
- Clear source/provenance for external job data.
- Secure server-side AI/API credentials; never embed secrets in APK.

## Phase 5 — Reliability
- Unit tests for scoring, parsing, profile merging, and interview evaluation.
- Instrumentation tests for critical UI flows.
- Release build validation after every meaningful feature.
- Versioned APK artifacts for live testing.

## Definition of done
A feature is not considered complete until its implementation is committed, automated build passes, relevant tests pass, and the release artifact is verified.
