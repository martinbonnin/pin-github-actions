# 📌 pin-github-actions

Using a branch name or tag name as a version for a GitHub action is dangerous as neither branches nor tags are immutable. (See [GitHub's documentation](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions#using-third-party-actions) for more details) 

`pin-github-actions` is a small command-line tool that replaces ("pins") branches or tag names with their corresponding commit sha.

You can see it in action [in this commit](https://github.com/martinbonnin/pin-github-actions/commit/010942c1197441c0d329e167020a29482d1c43bc):

```diff
--- a/.github/workflows/pr.yaml
+++ b/.github/workflows/pr.yaml
@@ -7,9 +7,9 @@ jobs:
     runs-on: macos-12
 
     steps:
-      - uses: actions/checkout@v3
-      - uses: gradle/gradle-build-action@v2.2.1
-      - uses: gradle/wrapper-validation-action@v1
+      - uses: actions/checkout@d0651293c4a5a52e711f25b41b05b2212f385d28 #v3
+      - uses: gradle/gradle-build-action@67421db6bd0bf253fb4bd25b31ebb98943c375e1 #v2.2.1
+      - uses: gradle/wrapper-validation-action@e6e38bacfdf1a337459f332974bb2327a31aaf4b #v1
       - run: ./gradlew build
         env:
```

### Installation

```
brew install martinbonnin/repo/pin-github-actions
```

### Usage

```
Usage: pin-github-actions [OPTIONS] [PATHS]...

Options:
  --version
  --login
  --logout
  --update    update to the latest known tag instead of just using the current
  -h, --help  Show this message and exit

Arguments:
  PATHS  The yaml files to process. You can also pass a directory
         in which case it will process all yaml files in that directory.
```

### Frequently Asked Questions

**Q**: Why pinning the first party actions like `actions/checkout@v3`? GitHub runs the actions, so it should be trusted by construction?

**A**: It's true that GitHub has to be trusted to run the actions. Nevertheless, no one is immune to exploits and in the advent that GitHub gets hacked, pinning the GitHub actions reduces a tiny little bit the attack surface. What's more, it makes the yaml files more consistent.

**Q**: Can I have a GitHub action that automatically updates the pins? 

**A**: Dependabot and Renovate do this. (albeit with [a caveat in the dependabot case](https://github.com/dependabot/dependabot-core/issues/4691))

