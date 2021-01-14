# How to release
This page describes roughly how we do a Play Store release.

## App/Code setup
* Increase `versionCode` and `versionName` in the [`app/build.gradle.kts`](app/build.gradle.kts).
  * Commit with `git commit -m "Increase versionCode and versionName"`
  * Push to `master`
* Create a **git tag**
  * Create with `git tag [NAME]`. Name should be `vX.Y.Z` meanwhile we follow the [semver](https://semver.org/) convention.
  * Push the tag

## GitHub
* Create a new **draft** [GitHub release](https://github.com/StefMa/Tino/releases)
  * Upload the files from our GitHub Action (zip files containing `debug.apk`, `release.aab` and `mapping file`)
  * Create release notes meanwhile each noteworthy change is a merge from a feature branch
    * The notes should contain **all** changes. User facing- and dev-changes.
    * Add a `Diff` in the format of: https://github.com/StefMa/Tino/compare/v1.1.0...v1.1.1 
  * Tick the `This is a pre-release` checkbox

## Play Store
* Create a new **Closed Alpha Track** track release
* Upload the `release.aab` from the GitHub Action
* Add only *user facing* changes to the changelog

Once we decided that this release is stable enough, move it to the Production Track.
Also move the **draft GitHub release** to a "real release".
