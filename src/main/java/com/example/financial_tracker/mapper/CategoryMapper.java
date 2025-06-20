package com.example.financial_tracker.mapper;

import com.example.financial_tracker.dto.CategoryDTO;
import com.example.financial_tracker.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  @Mapping(source = "user.id", target = "userId")
  CategoryDTO toDto(Category category);


  @Mapping(source = "userId", target = "user.id")
  Category toEntity(CategoryDTO categoryDto);

  List<CategoryDTO> toDtoList(List<Category> categories);
}
