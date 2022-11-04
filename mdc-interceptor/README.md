# MDC

---

**MDC** stands for ***Mapped Diagnostic Context*** and is used to enrich the log messages by adding some information on to it.
It comes as part of ```org.slf4j``` package, as *MDC* class.

- So, we will add this correlation ID using MDC to our system.

- Any service that includes this module will automatically check each request, if MDC header is there, and if it's not, creates a new ID to be used in MDC.

- And since we already added correlationID to logback, this header will be automatically included in each log message.