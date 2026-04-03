package com.demo.dto;

import java.util.List;

/**
 * Generic paginated response envelope returned by admin list endpoints.
 *
 * @param <T> the element type
 */
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content       = content;
        this.page          = page;
        this.size          = size;
        this.totalElements = totalElements;
        this.totalPages    = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        this.last          = page >= this.totalPages - 1;
    }

    public List<T>  getContent()       { return content; }
    public int      getPage()          { return page; }
    public int      getSize()          { return size; }
    public long     getTotalElements() { return totalElements; }
    public int      getTotalPages()    { return totalPages; }
    public boolean  isLast()           { return last; }
}
