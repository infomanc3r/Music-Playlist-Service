package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeValueException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);

        if (!MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getName())
                || !MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getCustomerId())) {
            throw new InvalidAttributeValueException("Name or customer ID contains invalid characters!");
        }

        Playlist playlist = playlistDao.getPlaylist(updatePlaylistRequest.getId());

        if (playlist == null) {
            throw new PlaylistNotFoundException("Playlist not found!");
        }

        if (!Objects.equals(updatePlaylistRequest.getCustomerId(), playlistDao.getPlaylist(updatePlaylistRequest.getId()).getCustomerId())) {
            throw new InvalidAttributeChangeException("Customer Ids do not match!");
        }

        // playlistDao.savePlaylist(playlistDao.getPlaylist(updatePlaylistRequest.getId()));

        playlist.setName(updatePlaylistRequest.getName());
        playlistDao.savePlaylist(playlist);

        PlaylistModel model = null;

        if (playlist.getTags() != null) {
            List<String> modelTags = new ArrayList<>(playlist.getTags());

            model = PlaylistModel.builder()
                    .withId(playlist.getId())
                    .withCustomerId(playlist.getCustomerId())
                    .withName(playlist.getName())
                    .withTags(modelTags)
                    .withSongCount(playlist.getSongCount())
                    .build();
        } else {
            model = PlaylistModel.builder()
                    .withId(playlist.getId())
                    .withCustomerId(playlist.getCustomerId())
                    .withName(playlist.getName())
                    .withSongCount(playlist.getSongCount())
                    .build();
        }

        return UpdatePlaylistResult.builder()
                .withPlaylist(model)
                .build();
    }
}
