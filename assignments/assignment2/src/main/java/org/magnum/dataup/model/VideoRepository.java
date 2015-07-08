package org.magnum.dataup.model;

import java.util.Collection;

public interface VideoRepository {

    Video getVideo(long id);

    Collection<Video> getVideos();

    boolean addVideo(Video v);

    Collection<Video> findByTitle(String title);
}
