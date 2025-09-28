package com.redbus.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {
    private List<JourneySearchResultDto> journeys;
    private Integer totalResults;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private SearchFiltersDto appliedFilters;
}
