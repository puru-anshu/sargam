package com.arutech.sargam.data.inject;


import com.arutech.sargam.activity.AboutActivity;
import com.arutech.sargam.activity.BaseActivity;
import com.arutech.sargam.activity.MainActivity;
import com.arutech.sargam.activity.SearchActivity;
import com.arutech.sargam.activity.WebActivity;
import com.arutech.sargam.activity.WebSearchActivity;
import com.arutech.sargam.activity.instance.AlbumActivity;
import com.arutech.sargam.activity.instance.ArtistActivity;
import com.arutech.sargam.activity.instance.AutoPlaylistActivity;
import com.arutech.sargam.activity.instance.AutoPlaylistEditActivity;
import com.arutech.sargam.activity.instance.GenreActivity;
import com.arutech.sargam.activity.instance.PlaylistActivity;
import com.arutech.sargam.activity.instance.TrackGroupActivity;
import com.arutech.sargam.adapter.LibraryEmptyState;
import com.arutech.sargam.dialog.AppendPlaylistDialogFragment;
import com.arutech.sargam.dialog.CreatePlaylistDialogFragment;
import com.arutech.sargam.dialog.PlaylistCollisionDialogFragment;
import com.arutech.sargam.fragments.AlbumFragment;
import com.arutech.sargam.fragments.ArtistFragment;
import com.arutech.sargam.fragments.DirectoryListFragment;
import com.arutech.sargam.fragments.EqualizerFragment;
import com.arutech.sargam.fragments.GenreFragment;
import com.arutech.sargam.fragments.MiniplayerFragment;
import com.arutech.sargam.fragments.NowPlayingFragment;
import com.arutech.sargam.fragments.PlaylistFragment;
import com.arutech.sargam.fragments.PreferenceFragment;
import com.arutech.sargam.fragments.QueueFragment;
import com.arutech.sargam.fragments.SongFragment;
import com.arutech.sargam.fragments.web.TrackGroupFragment;
import com.arutech.sargam.fragments.web.WebSongFragment;
import com.arutech.sargam.player.ServicePlayerController;
import com.arutech.sargam.viewmodel.AlbumViewModel;
import com.arutech.sargam.viewmodel.ArtistViewModel;
import com.arutech.sargam.viewmodel.BaseLibraryActivityViewModel;
import com.arutech.sargam.viewmodel.GenreViewModel;
import com.arutech.sargam.viewmodel.MiniplayerViewModel;
import com.arutech.sargam.viewmodel.NowPlayingArtworkViewModel;
import com.arutech.sargam.viewmodel.NowPlayingControllerViewModel;
import com.arutech.sargam.viewmodel.PlaylistViewModel;
import com.arutech.sargam.viewmodel.RuleHeaderViewModel;
import com.arutech.sargam.viewmodel.RuleViewModel;
import com.arutech.sargam.viewmodel.SongViewModel;
import com.arutech.sargam.viewmodel.web.TrackGroupViewModel;
import com.arutech.sargam.viewmodel.web.WSongViewModel;
import com.arutech.sargam.widgets.BaseWidget;

public interface SargamGraph {
	void inject(com.arutech.sargam.player.MusicPlayer musicPlayer);


	void inject(NowPlayingFragment nowPlayingFragment);

	void inject(MiniplayerViewModel miniplayerViewModel);

	void inject(BaseLibraryActivityViewModel baseLibraryActivityViewModel);

	void inject(GenreViewModel genreViewModel);

	void inject(ArtistViewModel artistViewModel);

	void inject(PlaylistViewModel playlistViewModel);

	void inject(SongViewModel songViewModel);

	void inject(RuleViewModel ruleViewModel);

	void inject(EqualizerFragment equalizerFragment);

	void inject(AlbumFragment albumFragment);

	void inject(DirectoryListFragment directoryListFragment);

	void inject(GenreFragment genreFragment);

	void inject(MiniplayerFragment miniplayerFragment);

	void inject(PlaylistFragment playlistFragment);

	void inject(PreferenceFragment preferenceFragment);

	void inject(QueueFragment queueFragment);

	void inject(SongFragment songFragment);

	void inject(AppendPlaylistDialogFragment appendPlaylistDialogFragment);

	void inject(CreatePlaylistDialogFragment createPlaylistDialogFragment);

	void inject(PlaylistCollisionDialogFragment playlistCollisionDialogFragment);

	void injectBaseActivity(BaseActivity baseActivity);

	void inject(MainActivity mainActivity);

	void inject(SearchActivity searchActivity);

	void inject(AutoPlaylistActivity autoPlaylistActivity);

	void inject(AboutActivity aboutActivity);

	void inject(AlbumActivity albumActivity);

	void inject(ArtistActivity artistActivity);

	void inject(AutoPlaylistEditActivity autoPlaylistEditActivity);

	void inject(GenreActivity genreActivity);

	void inject(PlaylistActivity playlistActivity);

	void inject(AlbumViewModel albumViewModel);

	void inject(NowPlayingArtworkViewModel nowPlayingArtworkViewModel);

	void inject(NowPlayingControllerViewModel nowPlayingControllerViewModel);

	void inject(LibraryEmptyState libraryEmptyState);

	void inject(ArtistFragment artistFragment);

	void inject(RuleHeaderViewModel ruleHeaderViewModel);

	void inject(ServicePlayerController.Listener listener);

	void inject(BaseWidget baseWidget);

	void inject(WSongViewModel wSongViewModel);

	void inject(TrackGroupViewModel trackGroupViewModel);

	void inject(TrackGroupActivity trackGroupActivity);

	void inject(WebActivity webActivity);

	void inject(WebSongFragment webSongFragment);

	void inject(TrackGroupFragment trackGroupFragment);

	void inject(WebSearchActivity webSearchActivity);

}
