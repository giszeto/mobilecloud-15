package org.magnum.dataup.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class NonPersistentVideoRepository implements VideoRepository {
    private static final AtomicLong currentId = new AtomicLong(0L);

    private Map<Long, Video> videos = new HashMap<>();

    /**
     * Get a specific video from the collection.
     *
     * @param id The unique identifier for the video.
     * @return The Video found in the collection.
     */
    @Override
    public Video getVideo(long id) {
        return videos.get(id);
    }

    /**
     * Add the Video into the collection of saved videosList.
     *
     * @param v The Video to be added to the collection.
     * @return True if successfully added video to the collection, false otherwise.
     */
    @Override
    public boolean addVideo(Video v) {
        Video saved = save(v);
        saved.setDataUrl(getDataUrl(saved.getId()));
        return v.equals(saved) ? true : false;
    }

    /**
     * Get all the Videos saved in the repository.
     *
     * @return The collection of videos in the repository.
     */
    @Override
    public Collection<Video> getVideos() {
        return videos.values();
    }

    /**\
     * Find all the Videos that match the title.
     *
     * @param title The String to search for in the collection of Videos.
     * @return The collection of Videos that match the title.
     */
    @Override
    public Collection<Video> findByTitle(String title) {
        Set<Video> matches = new HashSet<>();
        for (Video v : videos.values()) {
            if (title.equals(v.getTitle())) {
                matches.add(v);
            }
        }
        return matches;
    }

    private Video save(Video entity) {
        checkAndSetId(entity);
        videos.put(entity.getId(), entity);
        return entity;
    }

    private void checkAndSetId(Video entity) {
        if(entity.getId() == 0){
            entity.setId(currentId.incrementAndGet());
        }
    }

    private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
            "http://"+request.getServerName()
                + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
        return base;
    }
}
