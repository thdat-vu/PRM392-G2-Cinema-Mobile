package com.g2.moviebooking.data.remote.model.Response;

import com.g2.moviebooking.data.remote.model.Entity.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private MovieData data;

    public boolean isSuccess() {
        return success;
    }

    public MovieData getData() {
        return data;
    }

    public static class MovieData {
        @SerializedName("pageData")
        private List<Movie> pageData;

        @SerializedName("pageInfo")
        private PageInfo pageInfo;

        public List<Movie> getPageData() {
            return pageData;
        }

        public PageInfo getPageInfo() {
            return pageInfo;
        }
    }

    public static class PageInfo {
        @SerializedName("pageNum")
        private int pageNum;

        @SerializedName("pageSize")
        private int pageSize;

        @SerializedName("totalItems")
        private int totalItems;

        @SerializedName("totalPages")
        private int totalPages;

        public int getPageNum() { return pageNum; }
        public int getPageSize() { return pageSize; }
        public int getTotalItems() { return totalItems; }
        public int getTotalPages() { return totalPages; }
    }
}