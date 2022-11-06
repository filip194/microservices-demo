# MDC

---

**MDC** stands for ***Mapped Diagnostic Context*** and is used to enrich the log messages by adding some information onto them.
It comes as part of ```org.slf4j``` package, as *MDC* class.

- So, we will add log message correlation ID using MDC to our system.

- Any service that includes this module will automatically check each request if MDC header is there, and if it's not, creates a new ID to be used in MDC. Also, adds header ```X-Correlation-ID``` with correlation ID value.

- And since we already added correlationID to logback, this header will be automatically included in each log message.