# Quarkus Cert-Manager
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.certmanager/quarkus-certmanager?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.certmanager/quarkus-certmanager)

## Introduction

This Quarkus extension simplifies the generation of the Kubernetes manifests for configuring [Cert-Manager](https://cert-manager.io/) issuers, including:
- [SelfSigned](https://cert-manager.io/docs/configuration/selfsigned/) (PKI),
- [Vault](https://cert-manager.io/docs/configuration/vault/),
- [CA](https://cert-manager.io/docs/configuration/ca/)

It works with the CNCF [Cert-Manager](https://cert-manager.io/) project running on a Kubernetes cluster.

## Documentation

The documentation for this extension can be found [here](https://quarkiverse.github.io/quarkiverse-docs/quarkus-certmanager/dev/index.html).

## Compatibility with Quarkus

| Quarkus Cert-Manager Version  | Quarkus Version |
|---|---|
| 1.0.1  | Quarkus 3+ |
| 1.0.0  | Quarkus 3+ |
| 0.0.2  | Quarkus 2.12+ |
| 0.0.1  | Quarkus 2.12+ |

## Testing

The integration tests run on a [KinD](https://kind.sigs.k8s.io/) Kubernetes cluster with [Cert-Manager](https://cert-manager.io/) installed.
The project uses the Cert-Manager [Long Term Support (LTS)](https://cert-manager.io/docs/releases/#long-term-support-releases) release, currently **v1.17.4**.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/Sgitario"><img src="https://avatars.githubusercontent.com/u/6310047?v=4&s=100" width="100px;" alt=""/><br /><sub><b>Jose Carvajal</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-certmanager/commits?author=Sgitario" title="Code">💻</a> <a href="#maintenance-sgitario" title="Maintenance">🚧</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!