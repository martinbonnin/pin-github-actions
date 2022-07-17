# 📌 pin-github-actions

Using a branch name or tag name as a version for a GitHub action is dangerous as neither branches nor tags are immutable. (See [GitHub's documentation](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions#using-third-party-actions) for more details) 

`pin-github-actions` is a small command-line tool that replaces ("pins") branches or tag names with their corresponding commit sha.

```diff

```


### Installation


### Frequently Asked Questions

Q: Why pinning the first party actions like `actions/checkout@v3`? GitHub runs the actions, so it should be trusted by construction?
A: It's true that GitHub has to be trusted to run the actions. Nevertheless, no one is immune to exploits and in the advent that GitHub gets hacked, pinning the GitHub actions reduces a tiny little bit the attack surface. What's more, it makes the yaml files more consistent.

Q: Can I have a GitHub action that automatically updates the pins? 
A: Dependabot and Renovate do this. (albeit with [a caveat in the dependabot case](https://github.com/dependabot/dependabot-core/issues/4691))

