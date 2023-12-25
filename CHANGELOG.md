
## [hive4j-parent-0.0.12] - 2023-12-25
### :sparkles: New Features
- [`2a55103`](https://github.com/kettoleon/hive4j/commit/2a5510331077ba09beeab49b2bf8679447cb8421) - Spinner while query websocket is waiting for the first String. *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`c5f76af`](https://github.com/kettoleon/hive4j/commit/c5f76af6d8cccf81e5e95eacdf5e3081c54701dc) - Tried different strategies to beautify model output, but settled with a simple Markdown to html renderer. *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`d5fadf5`](https://github.com/kettoleon/hive4j/commit/d5fadf5ec0c5e71acedbc75375d42477a154e2f0) - Made calls for generation to Ollama clients to block until the previous one has finished. *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`d72e232`](https://github.com/kettoleon/hive4j/commit/d72e23225b0a9540e9ac36d4aa4153c695f3884f) - added spring session jdbc, to keep sessions between restarts of application *(commit by [@enr02](https://github.com/enr02))*
- [`91feb67`](https://github.com/kettoleon/hive4j/commit/91feb6771c81e4e46d7ceeee9b139828f78d2690) - added LlamaInstructionSerializer to support mistral, llama, and llama2 family models. *(commit by [@enr02](https://github.com/enr02))*
- [`4a1577b`](https://github.com/kettoleon/hive4j/commit/4a1577b8188e0029e6f27ddd6c67371aece207ee) - Won't fail with a fresh installation that does not have any backend configured. *(commit by [@enr02](https://github.com/enr02))*
- [`42536b3`](https://github.com/kettoleon/hive4j/commit/42536b383241b1a0cd71b8394873fca418fc6397) - Began to add code syntax highlighting. *(commit by [@enr02](https://github.com/enr02))*
- [`a5686e3`](https://github.com/kettoleon/hive4j/commit/a5686e368ec57727dd674f065603f4fa92ea54f9) - updated to support ollama v0.1.17 *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`acbf577`](https://github.com/kettoleon/hive4j/commit/acbf57797fef0bb90c9f9f3eafa51b10f2f26e09) - customised rounding for model downloading status *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`c13bb38`](https://github.com/kettoleon/hive4j/commit/c13bb384293f789e4a7d8d05e5c08833029b5bc0) - added a log message that tells how fast the generation went (tokens/second) (To check different models in my computer) *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`78709e2`](https://github.com/kettoleon/hive4j/commit/78709e25e15cc074d21fe52dd10da43987a745fb) - Exposing the generation progress to the app and ui layers *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`1a9934a`](https://github.com/kettoleon/hive4j/commit/1a9934a5ec24920bac332ac9a43535ba651b92d9) - Began to add support for ssh port forwarding *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`b5e9647`](https://github.com/kettoleon/hive4j/commit/b5e9647ee814fbcb62f84659988bc6ad6d36a491) - Began to add support for ssh port forwarding *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`95fe6db`](https://github.com/kettoleon/hive4j/commit/95fe6db272c8f5e5feecd572d106792a216de177) - added support for ssh port forwarding *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`15f0e03`](https://github.com/kettoleon/hive4j/commit/15f0e03b116e58caf74f9b74b086968844adc385) - added light/dark theme switch *(commit by [@kettoleon](https://github.com/kettoleon))*

### :bug: Bug Fixes
- [`87d321f`](https://github.com/kettoleon/hive4j/commit/87d321f9520d01381bcc40214a75118ddf215b0f) - doing some html cleanup *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`8de955c`](https://github.com/kettoleon/hive4j/commit/8de955c9fa5eacafe139f3bb28e1ab368a5fb8b2) - reverting to java 21 *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`a2671f3`](https://github.com/kettoleon/hive4j/commit/a2671f3992c6e85519adc168d0ccab3179e93107) - fixing pom *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`482f947`](https://github.com/kettoleon/hive4j/commit/482f9479ca5285d6df5df3bfd371c0f7ad2649a5) - highlighting code as it appears live *(commit by [@kettoleon](https://github.com/kettoleon))*


## [hive4j-parent-0.0.11] - 2023-12-06
### :sparkles: New Features
- [`8dc2503`](https://github.com/kettoleon/hive4j/commit/8dc2503caf8dae82996db2869319f3f43579580d) - Gave the QueryFunction the ability to set the system prompt of the agent when using the model. *(commit by [@kettoleon](https://github.com/kettoleon))*


## [hive4j-parent-0.0.9] - 2023-12-04
### :bug: Bug Fixes
- [`4a673aa`](https://github.com/kettoleon/hive4j/commit/4a673aafb205373224cf6b274c7b43d76bcf60e6) - trying to fix the github action for release again *(commit by [@kettoleon](https://github.com/kettoleon))*


## [hive4j-parent-0.0.6] - 2023-12-03
### :sparkles: New Features
- [`49b57eb`](https://github.com/kettoleon/hive4j/commit/49b57eb1da43665e79f944787f9ee494b9a1d279) - Adding configurable ollama.url property *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`5606755`](https://github.com/kettoleon/hive4j/commit/560675509aabd514d896b97e460c7b49514bc69f) - Adding ollama model management (list/pull/delete) *(commit by [@kettoleon](https://github.com/kettoleon))*
- [`586c8b3`](https://github.com/kettoleon/hive4j/commit/586c8b31f93a71fac9ddec0b67c3f787c6c1d4f3) - Added the concept of LLM backends *(commit by [@kettoleon](https://github.com/kettoleon))*


## [hive4j-parent-0.0.5] - 2023-12-01
### :bug: Bug Fixes
- [`2753d03`](https://github.com/kettoleon/hive4j/commit/2753d035a47e6d6f8ed4e2a1db8c3ed38415656e) - Trying to fix release build action *(commit by [@kettoleon](https://github.com/kettoleon))*


[hive4j-parent-0.0.5]: https://github.com/kettoleon/hive4j/compare/hive4j-parent-0.0.4...hive4j-parent-0.0.5
[hive4j-parent-0.0.6]: https://github.com/kettoleon/hive4j/compare/hive4j-parent-0.0.5...hive4j-parent-0.0.6
[hive4j-parent-0.0.9]: https://github.com/kettoleon/hive4j/compare/hive4j-parent-0.0.8...hive4j-parent-0.0.9
[hive4j-parent-0.0.11]: https://github.com/kettoleon/hive4j/compare/hive4j-parent-0.0.10...hive4j-parent-0.0.11
[hive4j-parent-0.0.12]: https://github.com/kettoleon/hive4j/compare/hive4j-parent-0.0.11...hive4j-parent-0.0.12