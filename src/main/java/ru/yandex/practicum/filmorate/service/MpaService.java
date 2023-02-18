package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class MpaService {

    private final FilmStorage filmStorage;

    public MpaService(@Qualifier("filmDBStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        filmStorage.checkMpaInDb(id);
        return filmStorage.getMpaById(id);
    }
}
