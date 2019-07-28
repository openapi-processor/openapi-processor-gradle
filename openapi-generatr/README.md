# openapi-generatr

# com.github.hauner.openapi.api

provides an interface that can be used to consume an openapi-generatr-<id>, e.g. by using the ServiceLoader. See the gradle plugin. 

# com.github.hauner.openapi.gradle 

provides a gradle plugin that uses com.github.hauner.openapi.api.OpenApiGeneratr to load the available openapi-generatrs from the classpath.

For any found generatr it will add a `generate<id.capitalize()>Api` task and a 'generatr<id.capitalize()>' configuration object to the gradle
project. The options objects class is provided by the `OpenApiGeneratr.getOptionsType()` method.

For example given a generatr with the name openapi-generatr-spring the gradle project will get a
 - `generateSpringApi` gradle task
 - and an `generatrSpring` options object

See the 'gradle-plugin-sample' project.
