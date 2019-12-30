[![][badge-license]][generatr-license]
[![][badge-ci]][workflow-ci]

# com.github.hauner.openapi.gradle 

a gradle plugin based on the [openapi-generatr-api][generatr-api] to handle all available openapi-generatrs from the classpath.

For any found generatr it will add a `generate<id.capitalize()>` task and a `generatr<id.capitalize()>` configuration object to the
gradle project. The options objects class is provided by the `OpenApiGeneratr.getOptionsType()` method.

For example given a generatr with the name `openapi-generatr-spring` the gradle project will get a
 - `generateSpring` gradle task
 - and an `generatrSpring` options object

# Sample project

See [`openapi-generatr-spring-mvc-sample`][generatr-spring-mvc] for a complete spring boot sample project.


[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[generatr-license]: https://github.com/hauner/openapi-generatr-gradle/blob/master/LICENSE
[badge-ci]: https://github.com/hauner/openapi-generatr-gradle/workflows/ci/badge.svg
[workflow-ci]: https://github.com/hauner/openapi-generatr-gradle/actions?query=workflow%3Aci

[generatr-api]: https://github.com/hauner/openapi-generatr-api
[generatr-spring-mvc]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
