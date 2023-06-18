package imd.ufrn.br.purposesong.view.session;

import imd.ufrn.br.purposesong.App;
import imd.ufrn.br.purposesong.database.RepositoryFactory;
import imd.ufrn.br.purposesong.entity.Folder;
import imd.ufrn.br.purposesong.entity.Song;
import imd.ufrn.br.purposesong.player.SongPlayer;
import imd.ufrn.br.purposesong.use_case.AddFolder;
import imd.ufrn.br.purposesong.use_case.AddSong;
import imd.ufrn.br.purposesong.use_case.GetAllSongsOfFolder;
import imd.ufrn.br.purposesong.use_case.GetAllSongsOfUser;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongStore {
    private App app = App.getInstance();
    private UserStore userStore = UserStore.getInstance();

    private SongPlayer player = SongPlayer.getInstance();

    public ListProperty<Song> songs = new SimpleListProperty<Song>(FXCollections.observableArrayList());
    public ListProperty<String> songNames = new SimpleListProperty<String>(FXCollections.observableArrayList());

    private String DEFAULT_SONG_IMAGE = "file:src/main/resources/imd/ufrn/br/purposesong/images/default.png";


    // Player
    public void playSong(Song song) {
        this.player.play(song);
    }

    public void stopSong() {
        this.player.pause();
    }


    // Gerencia as property na memoria
    public void appendSongList(Song song) {
        var newValue = new ArrayList<Song>(this.songs);
        newValue.add(song);
        this.setSongList(newValue);
    }

    public void setSongList(List<Song> songs) {
        this.songs.setAll(songs);
        this.songNames.setAll(this.songs.stream().map(it -> it.name).toList());
    }

    public void resetStore() {
        SongStore.instance = new SongStore();
    }


    // Scan e busca de musicas
    public void fetchSongListOfCurrentUser() {
        var repo = RepositoryFactory.getSongRepository();
        var user = this.userStore.getUser().get();
        this.setSongList(new GetAllSongsOfUser(repo).execute(user));
    }

    public void fetchSongListOfScanInFolder(Folder folder) {
        List<Song> songsOfFolder = new GetAllSongsOfFolder().execute(folder);
        songsOfFolder.forEach(this::appendSongList);
    }


    // Salvar nos database
    public void saveSongFileInDB(File file) {
        var repo = RepositoryFactory.getSongRepository();

        var song = Song.fromFile(file);
        var user = this.userStore.getUser().get();
        song.setUserID(user.getId().get());
        new AddSong(repo).execute(song);

        this.appendSongList(song);
    }

    public void saveFolderInDB(File file) {
        var user = this.userStore.getUser().get();

        Folder folder = new Folder();
        folder.path = file.toPath().toString();
        folder.userID = user.getId().get();
        folder.scanSongFiles();

        var repo = RepositoryFactory.getFolderRepository();
        new AddFolder(repo).execute(folder);

        this.fetchSongListOfScanInFolder(folder);
    }

    // Singleton ---------------------------
    private static SongStore instance = new SongStore();

    private SongStore() {}

    public static SongStore getInstance() {
        return SongStore.instance;
    }

}
