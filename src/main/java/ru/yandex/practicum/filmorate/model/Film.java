package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Set<Long> getLikes() {
        return likes;
    }

    public void setLikes(Set<Long> likes) {
        this.likes = likes;
    }

    public Integer popularity() {
        return likes.size();
    }

    public void addLIke(Integer userId) {
        likes.add((long) userId);
    }

    public void removeLike(Integer userId) {
        if (likes.contains((long) userId)) {
            likes.remove((long) userId);
        } else {
            throw new EntityNotFoundException("Лайк от пользователя " + userId + " этому фильму и " +
                    "так не был поставлен, удалять нечего");
        }
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
