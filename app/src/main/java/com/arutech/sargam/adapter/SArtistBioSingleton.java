package com.arutech.sargam.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arutech.sargam.R;
import com.arutech.sargam.saavn.api.model.ArtistBio;

public class SArtistBioSingleton extends HeterogeneousAdapter.SingletonSection<ArtistBio> {


	private boolean mHasRelatedArtists = false;

	public SArtistBioSingleton(ArtistBio data) {
		super(data);
		mHasRelatedArtists = data != null && data.getSimilarArtists().size() > 0;

	}

	@Override
	public EnhancedViewHolder<ArtistBio> createViewHolder(HeterogeneousAdapter adapter,
	                                                      ViewGroup parent) {
		return new ViewHolder(
				LayoutInflater.from(parent.getContext())
						.inflate(R.layout.instance_artist_bio, parent, false),
				mHasRelatedArtists);
	}

	public static class ViewHolder extends EnhancedViewHolder<ArtistBio>
			implements View.OnClickListener {

		private TextView bioText;
		private Button lfmButton;
		private String artistURL;

		public ViewHolder(View itemView, boolean hasRelatedArtists) {
			super(itemView);

			bioText = (TextView) itemView.findViewById(R.id.artist_bio_content);
			lfmButton = (Button) itemView.findViewById(R.id.artist_bio_lfm_link);
			lfmButton.setOnClickListener(this);

			if (hasRelatedArtists) {
				((GridLayoutManager.LayoutParams) itemView.getLayoutParams()).bottomMargin = 0;
			}
		}

		@Override
		@SuppressLint("SetTextI18n")
		public void onUpdate(ArtistBio item, int sectionPosition) {
			String summary = item.getBio();
			bioText.setText(summary);

//            artistURL = item.get();
		}

		@Override
		public void onClick(View v) {
			if (v.equals(lfmButton)) {
				//Open Activity
			}
		}
	}
}
