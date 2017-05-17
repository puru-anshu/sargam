package com.arutech.sargam.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.arutech.sargam.databinding.InstanceSongQueueBinding;
import com.arutech.sargam.fragments.BaseFragment;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.viewmodel.QueueSongViewModel;

import java.util.List;

import timber.log.Timber;

public class QueueSection extends EditableSongSection {

    private BaseFragment mFragment;
    private PlayerController mPlayerController;

    public QueueSection(BaseFragment fragment, PlayerController playerController,
                        List<Song> data) {
        super(data);
        mFragment = fragment;
        mPlayerController = playerController;
    }

    @Override
    protected void onDrop(int from, int to) {
        if (from == to) return;

        // Calculate where the current song index is moving to
        mPlayerController.getQueuePosition().take(1).subscribe(nowPlayingIndex -> {
            int futureNowPlayingIndex;

            if (from == nowPlayingIndex) {
                futureNowPlayingIndex = to;
            } else if (from < nowPlayingIndex && to >= nowPlayingIndex) {
                futureNowPlayingIndex = nowPlayingIndex - 1;
            } else if (from > nowPlayingIndex && to <= nowPlayingIndex) {
                futureNowPlayingIndex = nowPlayingIndex + 1;
            } else {
                futureNowPlayingIndex = nowPlayingIndex;
            }

            // Push the change to the service
            mPlayerController.editQueue(mData, futureNowPlayingIndex);
        }, throwable -> {
            Timber.e(throwable, "Failed to drop queue item");
        });
    }

    @Override
    public EnhancedViewHolder<Song> createViewHolder(HeterogeneousAdapter adapter,
                                                     ViewGroup parent) {
        InstanceSongQueueBinding binding = InstanceSongQueueBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding, getData(), adapter);
    }

    public class ViewHolder extends EnhancedViewHolder<Song> {

        private InstanceSongQueueBinding mBinding;

        public ViewHolder(InstanceSongQueueBinding binding, List<Song> songList,
                          HeterogeneousAdapter adapter) {
            super(binding.getRoot());
            mBinding = binding;

            binding.setViewModel(new QueueSongViewModel(mFragment, songList,
                    adapter::notifyDataSetChanged));
        }

        @Override
        public void onUpdate(Song s, int sectionPosition) {
            mBinding.getViewModel().setSong(getData(), sectionPosition);
        }
    }
}
