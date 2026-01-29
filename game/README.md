Java Version Game
=================

![Screenshot](img/screenshot.png)

A simple game to learn and practice Java version features.

Alpha questions (Java 1.0-alpha1, Java 1.0-alpha2 and Java 1.0-alpha3) are shipped,
but disable by default.

You can find a version of the game online at [https://mostlynerdless.de/java-game](https://mostlynerdless.de/java-game).
The version [https://mostlynerdless.de/java-game#alpha](https://mostlynerdless.de/java-game#alpha) enables the alpha questions.

The source questions for the game are from the parent projects [test case files](../src/test/resources/feature_tests)
and the [alpha_features.json](./alpha_features.json) file.

Submitting New Questions
------------------------
To submit new questions, please open an issue on this repository
with the code snippet and the required Java version.

Build
-----

Run from the parent directory (check-language-version):

```shell
python3 game/generate_quiz.py --base-url https://mostlynerdless.de/java-game/ \
  --goatcounter https://goatcounter.mostlynerdless.de/
```

Contributing
------------
Feel free to suggest new code snippets or report issues via GitHub issues.

TODO
----
- link JEP for every feature (if available)

License
-------
Apache License 2.0