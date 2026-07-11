# Technical Report for Online Examination and Grading System

**Course:** INF811D: Object Oriented Programming <br>
<br>
**Programme:** MSc Information Technology<br>
<br>
**Institution:** University of Cape Coast (CoDE) <br>
<br>
**Application type:** JavaFX GUI desktop application<br>
<br>
**Student/Index Number:** Emmanuel Fosu (MS/ITE/25/0045)


> This report documents the design and implementation of the accompanying
> source code. It is provided as a working draft that mirrors the actual system.

---

## 1. Introduction

The Online Examination and Grading System is a desktop application that digitizes the
the examination workflow of a tertiary institution. It allows lecturers to author
timed assessments, students to sit those assessments under exam conditions, and the
system to mark objective answers automatically using the University of Cape Coast
grading standard. The application is built entirely with JavaFX and demonstrates the
the core principles of object-oriented programming.

## 2. Problem Statement

Traditional paper-based examinations are slow to mark, prone to human error during
grading, and offer no immediate feedback to students. Manual grade computation also introduces
introduces inconsistency in how raw scores map to letter grades. A software system
that captures answers electronically, marks them deterministically and applies a
single agreed grading scale removes these problems while enforcing exam conditions
such as time limits and single attempts.

## 3. Objectives of the System

- Provide secure, role-based access for administrators, instructors and students.
- Allow instructors to create and publish timed examinations with multiple question
  types.
- Present students with a timed, distraction-free examination interface.
- Grade objective questions automatically and apply the UCC grading scale.
- Give students instant feedback and a persistent record of their results.
- Validate all input and handle errors gracefully.

## 4. Scope of the System

The system covers account registration and authentication, exam authoring,
publishing, exam sitting with a countdown timer, automatic grading, result review
for both students and instructors, and basic user administration. Data is persisted
locally so that it survives between sessions. Out of scope are network/multi-machine
operation, essay-style human marking, and integration with an external student
information system.

## 5. Methodology

The application follows a layered architecture that separates concerns:

- **Model layer:** Plain Java objects representing the domain (users, exams,
  questions, submissions).
- **Service layer:** Application logic for authentication, user management, exam
  management and grading.
- **Persistence layer:** A serialization-backed data store.
- **Presentation layer:** JavaFX views, one per screen, coordinated by a central
  navigator.

Development was incremental: the domain model was defined first, then the services
and persistence, then the user interface, with a unit test pinning the grading
logic.

## 6. System Design

```
                     ┌─────────────────────────┐
                     │        OegsApp          │  (navigator + service wiring)
                     └───────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                         │
   ┌────▼────┐             ┌─────▼─────┐            ┌──────▼──────┐
   │ UI views│  ── call ─▶ │  Services │  ── use ─▶ │  DataStore  │
   └─────────┘             └─────┬─────┘            └─────────────┘
                                 │ operate on
                           ┌─────▼─────┐
                           │   Model   │
                           └───────────┘
```

The presentation layer never touches persistence directly; it goes through the
service layer, which in turn holds the only references to the data store. This keeps
the UI thin and the business rules testable in isolation.

## 7. Description of Classes and Methods

### Model
- **`User`:** *(abstract)* Common identity/credential state; abstract `getRole()`
  and `getDashboardTitle()`.
- **`Student` / `Instructor` / `Administrator`:** Concrete accounts adding
  role-specific fields.
- **`Role`:** *(enum)* The three account types.
- **`Gradable`:** *(interface)* The `grade(String)` contract.
- **`Question`:** *(abstract, implements `Gradable`)* Text and marks; abstract
  `getType()` / `isAutoGradable()`.
- **`MultipleChoiceQuestion` / `TrueFalseQuestion` / `ShortAnswerQuestion`:** Each
  overrides `grade(...)` with its own marking rule.
- **`Exam`:** A titled, timed collection of questions; computes total marks.
- **`Submission`:** A student's answers plus the resulting `GradeResult`.
- **`GradeResult`:** A mutable score/percentage/letter-grade/grade-point value.

### Services
- **`AuthService`:** `login(...)`, `logout()`, current-session tracking.
- **`UserService`:** `register(...)`, lookups, `seedDefaultsIfEmpty()`.
- **`ExamService`:** create/publish exams, add questions, submit and grade attempts,
  query results.
- **`GradingService`:** `grade(Exam, Submission)` and `classify(...)` implementing
  the UCC scale.

### Persistence & Utilities
- **`DataStore`:** in-memory collections mirrored to serialised `.dat` files.
- **`Validator`:** reusable field checks that throw `ValidationException`.
- **`PasswordUtil`:** salted SHA-256 password hashing and verification.
- **`IdGenerator`:** prefixed unique identifiers.

## 8. GUI Design Explanation

The interface uses a consistent visual language defined in a single stylesheet
(`styles.css`): a deep academic-blue top bar, white cards on a soft gradient
background, and clearly differentiated primary/ghost/danger buttons. Each role has a
dedicated dashboard. The exam-sitting screen is deliberately focused  on a header with
a live clock, a scrollable list of question cards, and a single submit action; so
students are not distracted during an assessment. The clock turns red in the final
minute to signal urgency. All errors are surfaced through modal dialogs with plain
language rather than raw exceptions.

## 9. OOP Concepts Implemented

- **Encapsulation:** All model fields are private and mutated only through methods
  that can enforce invariants; internal collections are returned as defensive
  copies (`Exam.getQuestions()`, `Submission.getAnswers()`).
- **Inheritance:** The `User` and `Question` hierarchies share state and behaviour
  from abstract bases, avoiding duplication.
- **Polymorphism:** `GradingService` iterates over `Question` references and calls
  `grade(...)` without knowing the concrete type; the exam-taking view renders the
  correct answer control by pattern-matching on subtype.
- **Abstraction:** The `Gradable` interface and abstract classes expose stable
  contracts (`grade`, `getType`) while hiding the specifics.
- **Additional practices:** Custom exception hierarchy, input validation,
  event-driven handlers, arrays/collections (`List`, `Map`), control structures and
  operators throughout the grading and validation logic.

## 10. Screenshots and Outputs

The screenshots are in the `docs/screenshots/` directory (11 screenshots but the main ones are listed here):

- `01-login.png`: sign-in screen
- `02-instructor-dashboard.png`: instructor's exam list
- `03-exam-builder.png`: adding questions
- `04-taking-exam.png`: timed exam with countdown
- `05-result.png`: instant grade feedback
- `06-admin.png`: user management

## 11. GitHub Repository Link

https://github.com/Fosukemmanuel/Online-Examination-and-Grading-System/tree/main

## 12. Challenges Encountered

wiring the countdown timer to
auto-submit, mapping percentages to grade boundaries without off-by-one errors,
persisting an object graph with serialization, etc

## 13. Conclusion

The system meets its objectives: it enforces role-based access, administers timed
examinations, grades objective answers automatically against the UCC scale, and
gives immediate, persistent feedback. Its layered, object-oriented design makes each
concern easy to reason about and extend.

## 14. Recommendations

Future work could add essay questions with instructor marking, question banks with
randomised selection, exports of results to PDF/CSV, and a networked
client–server deployment so multiple students can sit an exam concurrently from
different machines.

## 15. References

- Oracle, *JavaFX API Documentation*.
- University of Cape Coast, *Grading System / Academic Regulations*.
- Course lecture notes, INF811D — Object Oriented Programming.
