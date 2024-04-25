package com.example.moviejournal.Data;

public class Movie {
    private int _id;
    private String _title;
    private String _genre;
    private String _releaseYear;
    private String _description;

    public Movie() {}

    public Movie(int id, String _title, String _genre, String _releaseYear, String _description) {
        this._id = id;
        this._title = _title;
        this._genre = _genre;
        this._releaseYear = _releaseYear;
        this._description = _description;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_genre() {
        return _genre;
    }

    public void set_genre(String _genre) {
        this._genre = _genre;
    }

    public String get_releaseYear() {
        return _releaseYear;
    }

    public void set_releaseYear(String _releaseYear) {
        this._releaseYear = _releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "_title='" + _title + '\'' +
                ", _genre='" + _genre + '\'' +
                ", _releaseYear='" + _releaseYear + '\'' +
                '}';
    }
}
