package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.SavedSearchDTO;
import com.example.financial_tracker.dto.TransactionSearchDTO;
import com.example.financial_tracker.entity.SavedSearch;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.exception.ResourceNotFoundException;
import com.example.financial_tracker.repository.SavedSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SavedSearchService {

  private final SavedSearchRepository savedSearchRepository;
  private final ObjectMapper objectMapper;

  public SavedSearchDTO createSavedSearch(User user, SavedSearchDTO dto) {
    log.info("Creating saved search '{}' for user: {}", dto.getName(), user.getEmail());


    if (savedSearchRepository.existsByNameAndUser(dto.getName(), user)) {
      throw new BusinessLogicException("Saved search with this name already exists");
    }

    try {
      String searchCriteriaJson = objectMapper.writeValueAsString(dto.getSearchCriteria());

      SavedSearch savedSearch = SavedSearch.builder()
        .name(dto.getName())
        .searchCriteria(searchCriteriaJson)
        .user(user)
        .build();

      SavedSearch saved = savedSearchRepository.save(savedSearch);

      log.info("Successfully created saved search ID: {} for user: {}", saved.getId(), user.getEmail());
      return mapToDto(saved);

    } catch (Exception e) {
      log.error("Error creating saved search", e);
      throw new BusinessLogicException("Failed to create saved search");
    }
  }

  @Transactional(readOnly = true)
  public List<SavedSearchDTO> getUserSavedSearches(User user) {
    log.info("Fetching saved searches for user: {}", user.getEmail());

    List<SavedSearch> searches = savedSearchRepository.findByUserOrderByCreatedAtDesc(user);

    return searches.stream()
      .map(this::mapToDto)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public SavedSearchDTO getSavedSearchById(User user, Long id) {
    log.info("Fetching saved search ID: {} for user: {}", id, user.getEmail());

    SavedSearch savedSearch = savedSearchRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Saved search not found"));

    return mapToDto(savedSearch);
  }

  public void deleteSavedSearch(User user, Long id) {
    log.info("Deleting saved search ID: {} for user: {}", id, user.getEmail());

    SavedSearch savedSearch = savedSearchRepository.findByIdAndUser(id, user)
      .orElseThrow(() -> new ResourceNotFoundException("Saved search not found"));

    savedSearchRepository.delete(savedSearch);
    log.info("Successfully deleted saved search ID: {}", id);
  }

  private SavedSearchDTO mapToDto(SavedSearch savedSearch) {
    try {
      TransactionSearchDTO searchCriteria = objectMapper.readValue(
        savedSearch.getSearchCriteria(),
        TransactionSearchDTO.class
      );

      return SavedSearchDTO.builder()
        .id(savedSearch.getId())
        .name(savedSearch.getName())
        .searchCriteria(searchCriteria)
        .createdAt(savedSearch.getCreatedAt())
        .build();

    } catch (Exception e) {
      log.error("Error deserializing search criteria", e);
      throw new BusinessLogicException("Failed to read saved search");
    }
  }
}
