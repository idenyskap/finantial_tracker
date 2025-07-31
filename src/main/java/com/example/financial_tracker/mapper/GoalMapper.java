package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.GoalDTO;
import com.example.financial_tracker.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GoalMapper {

  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "categoryColor", source = "category.color")
  GoalDTO toDto(Goal entity);

  @Mapping(target = "category", ignore = true)
  @Mapping(target = "user", ignore = true)
  Goal toEntity(GoalDTO dto);

  List<GoalDTO> toDtoList(List<Goal> entities);
}
