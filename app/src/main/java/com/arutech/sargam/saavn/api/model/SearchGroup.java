package com.arutech.sargam.saavn.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by anshuman on 08/05/17.
 */
public class SearchGroup {

	@SerializedName("total")
	@Expose
	private Integer total;
	@SerializedName("start")
	@Expose
	private Integer start;
	@SerializedName("results")
	@Expose
	private List<TrackGroup> results = null;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public List<TrackGroup> getResults() {
		return results != null ? results : Collections.emptyList();
	}

	public void setResults(List<TrackGroup> results) {
		this.results = results;
	}
}
