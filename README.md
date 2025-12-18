# Base Repository – Protected Branches and Pull Request Rules

This repository demonstrates:
- Protected branches: `main` and `develop`.
- Pull Request rules: required reviews, CODEOWNERS, and CI status checks.

## Workflow

- `main`: stable; merge only via approved PRs.
- `develop`: integration; PRs target `develop`, then release to `main`.
- Feature branches: `feature/<name>` via PR.

## GitHub Setup

- Install the “Probot Settings” GitHub App for this repository.
- The `.github/settings.yml` enforces:
  - Required reviews (≥ 1)
  - CODEOWNERS reviews
  - Required status checks (CI)
  - No force-push; include administrators

## Presentation

See `docs/REPOSITORY_PRESENTATION.md` for checklists and evidence to capture.
