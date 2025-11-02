# List of Changes

## Layered architecture

Initially all of the UI, rules, and storage lived in Main. This created a cluttered mess. After the refactor we successfully split the packages into app/ (CLI), service/ (business rules), repo/ (interfaces), repo/csv/ (CSV impls), model/ (POJOs), util/ (config/validation/log), and exception/. This creates a modular system that makes it easier to test and modify as the need arises.

## Removal of global state

Previously we had a static students and courses class in Main alongside an auditlog. Now we have those states accessed via repositories, that are composed in Main, and injected into our RegistrationService. Because of these changes we now have a deterministic behavior, and much easier unit tests, where everything is not dependent on one fragile framework.

## Repository abstraction

Originally the file I/O was mixed in Main methods. Now we have multiple repository interfaces with CSV implementations. This helps with CSV in the immediate term, but could easily be implemented with JSON without touching higher layers.

## Service layer with business rules

With enroll and dropping behaviors embedded in the UI methods things became very quickly cluttered. Now a single RegistrationService class enforces rules on the program creating checks on capacity, duplicates, FIFO or queue waitlisting, and consistent updates to repos. With a single class handing out the rules for the rest of the program it becomes much more manageable for a developer to integrate new behaviors easily.

## Domain model clarified

With Student as an immutable record it remains as a constant for the program. In addition to this Course encapsulates the roster/waitlist with explicit, minimal mutators. The Models are data-centric, which frees them of I/O and UI mechanisms that clog up the flow of the program.

## CSV persistence kept (parity rule) but isolated

By allowing both Courses and Enrollments to keep their CSV formats we managed to keep the clarity of both while maintaining differentiators. While also allowing our CsvEnrollmentRepository.loadInto to reconstruct the roster/waitlist on startup thereby preserving the existing data of the program. This also allowed for a cleaner design.

## Graceful file handling

Utilizing the creation of files/directories on the first save was important to us. Thus we can skip and log corrupted lines while avoiding data loss from accidental crashes. This provides a robust interface for command line users.

## Validation centralized

Validation allows enforcement of rules on entry text fields or it throws a ValidationException. This allows consistent input checks with minimal use of regex only where necessary. (Regex is not our favorite, but it is useful.)

## Structured errors

We created out exceptions with their usage being all over the program in mind. In addition to this we allowed our CLI class to catche and prints error messages. This avoids println debugging and wastes less space in the program as a whole.

## Logging instead of println for diagnostics

We structured our util.Log to wrap our java.util.log for repo diagnostics. This kept our CLI clean and enables future log configuration for us.

## Externalized config

The structure of util.Config resolves storage.dir, csv.students, csv.courses, csv.enrollments from system props/env with sensible defaults. This allowed us to avoid hardcoded paths so the program is effectively machine agnostic.

## CLI isolated

Our module for the app.Cli handles menu & input which delegates to service. That way UI changes won’t touch rules/storage of the program.

## Entry point simplified

Thus our Main only wires components and has minimal code contained within. This helps with the overall composition of the program and maintains modularity.
