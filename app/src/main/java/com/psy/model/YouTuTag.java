package com.psy.model;

public class YouTuTag implements Comparable<YouTuTag>{
 private String tagName;
 private int tagConfidence;
 
public String getTagName() {
	return tagName;
}
public void setTagName(String tagName) {
	this.tagName = tagName;
}
public int getTagConfidence() {
	return tagConfidence;
}
public void setTagConfidence(int tagConfidence) {
	this.tagConfidence = tagConfidence;
}
@Override
public String toString() {
	return "YouTuTag [tagName=" + tagName + ", tagConfidence=" + tagConfidence
			+ "]";
}
@Override
public int compareTo(YouTuTag youTuTag) {
	return youTuTag.tagConfidence - this.tagConfidence;
}
 
}
