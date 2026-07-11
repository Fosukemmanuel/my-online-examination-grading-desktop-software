# Online Examination and Grading System

A secure, GUI-based **Online Examination and Grading System** built with **JavaFX**
as a Project by Emmanuel Fosu as an INF811D Object Oriented Programming requirement. It
automates the full assessment cycle: instructors author timed papers, students sit
them under a live countdown, and answers are marked instantly against the
**UCC-standardised grading scale** with immediate feedback.

---

## Features

- **Role-based access:** Three account types (Administrator, Instructor, Student),
  each connected to its own dashboard.
- **Exam authoring:** Instructors create timed exams and add three kinds of
  questions: multiple-choice, true/false and short-answer.
- **Timed examinations:** A live countdown clock; the exam auto-submits when time
  runs out.
- **Automatic grading:** Objective answers are marked the instant an exam is
  submitted; results map to UCC letter grades and grade points.
- **Instant feedback:** Students see their score, percentage and grade immediately,
  and can review a history of past attempts.
- **Results overview:** Instructors view every submission for an exam plus the
  class average.
- **User management:** Administrators review and remove accounts.
- **Persistence:** All data is saved locally between runs via Java serialization.
- **Input validation & exception handling:** Throughout,it validates data with friendly error
  dialogs instead of stack traces.

---
## To Get a Better View of the App
- There are screenshots of the system in the docs/screenshot directory
- Screenshots are also in the TECHNICAL REPORT. It contains more screenshots than the one in the docs/screenshot directory.

## UCC Grading Scale

| Percentage | Grade | Grade Point | Interpretation |
|-----------:|:-----:|:-----------:|----------------|
| 80 – 100   | A     | 4.0         | Excellent      |
| 75 – 79    | B+    | 3.5         | Very Good      |
| 70 – 74    | B     | 3.0         | Good           |
| 65 – 69    | C+    | 2.5         | Fairly Good    |
| 60 – 64    | C     | 2.0         | Average        |
| 55 – 59    | D+    | 1.5         | Below Average  |
| 50 – 54    | D     | 1.0         | Pass           |
| 45 – 49    | E     | 0.5         | Marginal Fail  |
| 0 – 44     | F     | 0.0         | Fail           |

---

## Technology

- **Java 21**
- **JavaFX 21** (controls + FXML)
- **Maven** build with the `javafx-maven-plugin`
- **JUnit 5** for unit tests

---

## Project Structure

```
online-examination-grading-system/
├── pom.xml
├── README.md
├── TECHNICAL_REPORT.md
├── docs/
│   └── screenshots/            # application screenshots
└── src/
    ├── main/
    │   ├── java/com/ucc/oegs/
    │   │   ├── Launcher.java            # plain entry point
    │   │   ├── OegsApp.java             # JavaFX Application + navigator
    │   │   ├── model/                   # domain model (OOP core)
    │   │   │   ├── User, Student, Instructor, Administrator
    │   │   │   ├── Question, MultipleChoiceQuestion, TrueFalseQuestion, ShortAnswerQuestion
    │   │   │   ├── Exam, Submission, GradeResult
    │   │   │   ├── Gradable (interface), Role (enum)
    │   │   ├── service/                 # AuthService, UserService, ExamService, GradingService
    │   │   ├── persistence/             # DataStore (serialisation)
    │   │   ├── exception/               # custom checked exceptions
    │   │   ├── util/                    # Validator, PasswordUtil, IdGenerator
    │   │   └── ui/                      # JavaFX views
    │   └── resources/
    │       └── styles.css
    └── test/
        └── java/com/ucc/oegs/service/GradingServiceTest.java
```

---

## Getting Started

### Prerequisites

- **JDK 21 or newer**
- **Maven 3.9+** (the JavaFX runtime is downloaded automatically as a dependency;
  no separate JavaFX SDK install is required)

### Download

Clone the repository with Git:

```bash
git clone https://github.com/<your-username>/online-examination-grading-system.git
cd online-examination-grading-system
```

Or, without Git, download the project as a ZIP from its GitHub page
(**Code → Download ZIP**), then unzip it and open a terminal in the extracted
`online-examination-grading-system` folder.

### Run

```bash
mvn clean javafx:run
```

### Run the tests

```bash
mvn test
```

### Build a runnable jar of the classes

```bash
mvn clean package
```

> If you prefer to run without the Maven plugin, download the JavaFX SDK and launch with:
> ```bash
> java --module-path /path/to/javafx-sdk/lib \
>      --add-modules javafx.controls,javafx.fxml \
>      -cp target/classes com.ucc.oegs.Launcher
> ```

---

## Demo Accounts

The first time the application starts it seeds one account per role:

| Role          | Username     | Password  |
|---------------|--------------|-----------|
| Administrator | `admin`      | `admin123`|
| Instructor    | `instructor` | `teach123`|
| Student       | `student`    | `study123`|

You can also register new accounts from the sign-in screen. Data is stored in a
local `data/` folder created on first run.

---

## How to Use

1. **Instructor:** sign in, click **New exam**, fill in the title/course/duration,
   then add questions of any type and set each answer key. Select the exam and
   click **Publish** to release it to students.
2. **Student:** sign in, open **Available Exams**, select a published exam and
   click **Start**. Answer the questions before the timer expires and submit; your
   grade appears instantly and is saved under **My Results**.
3. **Administrator:** sign in to review all accounts and remove any that are no
   longer needed.

---

## Object-Oriented Concepts Demonstrated

- **Encapsulation:** every model class keeps its fields private and exposes
  controlled accessors; collections are returned as defensive copies.
- **Inheritance:** `Student`, `Instructor` and `Administrator` extend the abstract
  `User`; the question types extend the abstract `Question`.
- **Polymorphism:** Tthe grading engine calls `Question.grade(...)` uniformly while
  each subclass supplies its own marking rule.
- **Abstraction:** The `Gradable` interface and the abstract base classes hide
  implementation detail behind stable contracts.

Check the Technical Report (TECHNICAL_REPORT__MS_ITE_25_0045.docx) for a full write-up.
