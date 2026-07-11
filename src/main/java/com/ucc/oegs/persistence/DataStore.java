package com.ucc.oegs.persistence;

import com.ucc.oegs.model.Exam;
import com.ucc.oegs.model.Submission;
import com.ucc.oegs.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * File-backed persistence layer.
 *
 * <p>Holds users, exams and submissions in in-memory collections and mirrors
 * each collection to a serialized {@code .dat} file under a local {@code data/}
 * directory. Loading happens once at start-up; every mutation re-saves the
 * affected file so state survives between runs.</p>
 *
 * <p>This is a desktop application, so a local serialized store is the natural
 * fit — there is no server process to host a database. All disk access is
 * wrapped in try/catch and surfaced as runtime state rather than crashing the
 * UI.</p>
 */
public class DataStore {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = "users.dat";
    private static final String EXAMS_FILE = "exams.dat";
    private static final String SUBMISSIONS_FILE = "submissions.dat";

    private final List<User> users = new ArrayList<>();
    private final List<Exam> exams = new ArrayList<>();
    private final List<Submission> submissions = new ArrayList<>();

    public DataStore() {
        new File(DATA_DIR).mkdirs();
        loadAll();
    }

    // ----- Users -------------------------------------------------------------

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public void updateUsers() {
        saveUsers();
    }

    public boolean removeUser(User user) {
        boolean removed = users.remove(user);
        if (removed) {
            saveUsers();
        }
        return removed;
    }

    // ----- Exams -------------------------------------------------------------

    public List<Exam> getExams() {
        return new ArrayList<>(exams);
    }

    public void addExam(Exam exam) {
        exams.add(exam);
        saveExams();
    }

    /** Persists in-place edits to an exam already held in the store. */
    public void updateExams() {
        saveExams();
    }

    public boolean removeExam(Exam exam) {
        boolean removed = exams.remove(exam);
        if (removed) {
            saveExams();
        }
        return removed;
    }

    // ----- Submissions -------------------------------------------------------

    public List<Submission> getSubmissions() {
        return new ArrayList<>(submissions);
    }

    public void addSubmission(Submission submission) {
        submissions.add(submission);
        saveSubmissions();
    }

    // ----- Persistence plumbing ---------------------------------------------

    private void loadAll() {
        users.addAll(this.<User>load(USERS_FILE));
        exams.addAll(this.<Exam>load(EXAMS_FILE));
        submissions.addAll(this.<Submission>load(SUBMISSIONS_FILE));
    }

    private void saveUsers() {
        save(USERS_FILE, users);
    }

    private void saveExams() {
        save(EXAMS_FILE, exams);
    }

    private void saveSubmissions() {
        save(SUBMISSIONS_FILE, submissions);
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> List<T> load(String fileName) {
        File file = new File(DATA_DIR, fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Warning: could not read " + fileName + " (" + ex.getMessage()
                    + "). Starting with an empty set.");
            return new ArrayList<>();
        }
    }

    private void save(String fileName, List<? extends Serializable> data) {
        File file = new File(DATA_DIR, fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(data));
        } catch (IOException ex) {
            System.err.println("Error: could not save " + fileName + " (" + ex.getMessage() + ").");
        }
    }
}
