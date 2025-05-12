package com.movieflix.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDTO> movieDtos, Integer pageNumber,
                                Integer pageSize , long totalElements,
                                int totalPages, boolean isLast) {
}
